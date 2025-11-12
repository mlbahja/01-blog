import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class ToastService {
  toastState = new BehaviorSubject<{ message: string; type: 'success' | 'error' | 'info' | null }>({
    message: '',
    type: null,
  });

  show(message: string, type: 'success' | 'error' | 'info' = 'info') {
    this.toastState.next({ message, type });
    setTimeout(() => this.clear(), 3000);
  }

  clear() {
    this.toastState.next({ message: '', type: null });
  }
}
