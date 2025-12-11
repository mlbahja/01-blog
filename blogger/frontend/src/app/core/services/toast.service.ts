import { Injectable } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';

@Injectable({
  providedIn: 'root',
})
export class ToastService {
  constructor(private snackBar: MatSnackBar) {}

  show(message: string, type: 'success' | 'error' | 'info' = 'info') {
    const panelClass = type === 'success' ? 'success-snackbar'
                     : type === 'error' ? 'error-snackbar'
                     : 'info-snackbar';

    this.snackBar.open(message, 'Close', {
      duration: 3000,
      horizontalPosition: 'right',
      verticalPosition: 'top',
      panelClass: [panelClass]
    });
  }

  // Keep clear method for backward compatibility
  clear() {
    this.snackBar.dismiss();
  }
}
