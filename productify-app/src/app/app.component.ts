import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router, RouterLink, RouterModule, RouterOutlet } from '@angular/router';
import { PanelMenuModule } from 'primeng/panelmenu';
import { ButtonModule } from 'primeng/button';
import { CommonModule } from '@angular/common';
import { MenuItem } from 'primeng/api';
import { Subscription } from 'rxjs';
import { AuthService } from './service/auth.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterModule, PanelMenuModule, ButtonModule, CommonModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css',
})
export class AppComponent implements OnInit, OnDestroy {
  title = 'productify-app';
  isLoggedIn = false;
  currentUsername: string | null = null;
  menuItems: MenuItem[] = [];

  private sub = new Subscription();

  constructor(public authService: AuthService, private router: Router) {}

  ngOnInit(): void {
    this.sub.add(
      this.authService.isLoggedIn$.subscribe((loggedIn) => {
        this.isLoggedIn = loggedIn;
        this.updateMenu();
      })
    );

    this.sub.add(
      this.authService.currentUser$.subscribe((user) => {
        this.currentUsername = user;
      })
    );
  }

  updateMenu(): void {
    if (this.isLoggedIn) {
      this.menuItems = [
        { label: 'Products', icon: 'pi pi-box', routerLink: ['/products'] },
        { label: 'Cart', icon: 'pi pi-shopping-cart', routerLink: ['/cart'] },
        { label: 'Orders', icon: 'pi pi-list', routerLink: ['/orders'] },
        { label: 'Add Product', icon: 'pi pi-plus', routerLink: ['/add-product'] },
      ];
    } else {
      this.menuItems = [
        { label: 'Products', icon: 'pi pi-box', routerLink: ['/products'] },
        { label: 'Sign In', icon: 'pi pi-sign-in', routerLink: ['/sign-in'] },
        { label: 'Sign Up', icon: 'pi pi-user-plus', routerLink: ['/signup'] },
      ];
    }
  }

  goToSignIn(): void {
    this.router.navigate(['/sign-in']);
  }

  goToSignUp(): void {
    this.router.navigate(['/signup']);
  }

  goToProducts(): void {
    this.router.navigate(['/products']);
  }

  signout(): void {
    this.authService.logout();
    this.router.navigate(['/sign-in']);
  }

  ngOnDestroy(): void {
    this.sub.unsubscribe();
  }
}
