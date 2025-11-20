import { CommonModule } from "@angular/common";
import { Component } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { UrlTree } from "@angular/router";
import { User } from "../../core/models/user.model";
import { UserService } from "../../core/services/user.service";

 //user-settings.component.ts
 @Component({
        selector : 'app-user-settings',
        standalone : true,
        imports : [CommonModule,FormsModule],
        templateUrl: './user-settings-component.html',
        styleUrls: ['./user-settings-component.css']
})
export class UserSettingsComponent {
    currentPassword  ='';
    confirmePassword = '';
    newPassword = '';
    loading = false;
    constructor UserSettingsComponent(private UserService : UserService, private auth){
        
    }
}