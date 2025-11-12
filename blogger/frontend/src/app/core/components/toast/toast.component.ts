import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ToastService } from '../../services/toast.service';

@Component({
  selector: 'app-toast',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './toast.component.html',
  styleUrls: ['./toast.component.css'],
})
export class ToastComponent implements OnInit {
  show = false;
  message = '';
  type: 'success' | 'error' | 'info' = 'info';
  icon = '';

  constructor(private toastService: ToastService) {}

  ngOnInit() {
    this.toastService.toastState.subscribe(({ message, type }) => {
      if (type) {
        this.message = message;
        this.type = type;
        this.icon =
          type === 'success'
            ? 'fa-check-circle'
            : type === 'error'
            ? 'fa-times-circle'
            : 'fa-info-circle';

        this.show = true;
        setTimeout(() => (this.show = false), 3000);
      }
    });
  }
}
