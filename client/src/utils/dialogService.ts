import {Injectable} from '@angular/core';
import {MatDialog} from '@angular/material/dialog';
import {Dialog} from '../components/dialog/dialog';

@Injectable({ providedIn: 'root' })
export class DialogService {
  constructor(private dialog: MatDialog) {
  }

  info(data: { title: string; message: string; }): void {
    this.dialog.open(Dialog, {
      width: '400px',
      data
    });
  }
}
