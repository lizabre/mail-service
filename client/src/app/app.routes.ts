import {Routes} from '@angular/router';
import {Registration} from '../pages/registration/registration';
import {Login} from '../pages/login/login';
import {Home} from '../pages/home/home';
import {authGuard} from '../guards/auth.guard';
import {publicGuard} from '../guards/public.guard';
import {Mail} from '../pages/mail/mail';
import {NewMail} from '../pages/new-mail/new-mail';

export const routes: Routes = [
  {path: 'register', component: Registration, canActivate: [publicGuard]},
  {path: 'login', component: Login, canActivate: [publicGuard]},
  {path: '', component: Home, canActivate: [authGuard]},
  {path: 'mail', component: Mail, canActivate: [authGuard]},
  {path: 'new-mail', component: NewMail, canActivate: [authGuard]},
  {path: '**', redirectTo: '/login'}
];
