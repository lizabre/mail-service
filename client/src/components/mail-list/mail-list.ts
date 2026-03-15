import {ChangeDetectorRef, Component, Input, OnInit} from '@angular/core';
import {MatNavList} from '@angular/material/list';
import {MatPaginator, PageEvent} from '@angular/material/paginator';
import {User} from '../../api/auth-service/auth.models';
import {AuthService} from '../../api/auth-service/auth.service';
import {MatButton} from '@angular/material/button';
import {MailListItem} from '../mail-list-item/mail-list-item';
import {NgForOf, NgIf} from '@angular/common';
import {MailService} from '../../api/mails-service/mails.service';
import {MailResponse} from '../../api/mails-service/mails.models';
import {Dialog} from '../dialog/dialog';
import {MatDialog} from '@angular/material/dialog';
import {MatIcon} from '@angular/material/icon';

/**
 * Component that displays a paginated list of mails for a given folder.
 * Supports deleting mails and reloading the list.
 */
@Component({
  selector: 'app-mail-list',
  imports: [
    MatNavList,
    MatPaginator,
    MatButton,
    MailListItem,
    NgForOf,
    NgIf,
    MatIcon,
  ],
  templateUrl: './mail-list.html',
  styleUrl: './mail-list.css',
})
export class MailList implements OnInit {
  @Input() folder: 'INBOX' | 'SENT' | 'DRAFTS' = 'INBOX';

  mails: MailResponse[] = [];
  pagedMails: MailResponse[] = [];
  user: User | null = null;

  pageSize = 5;
  pageIndex = 0;

  constructor(
    private authService: AuthService,
    private mailService: MailService,
    private cdr: ChangeDetectorRef,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.authService.currentUser$.subscribe(user => {
      this.user = user;
    });

    this.loadMails();
  }

  /** Fetches mails for the current folder and updates the paginated view. */
  loadMails(): void {
    this.mailService.getMails(this.folder).subscribe({
      next: (data) => {
        this.mails = data;
        this.updatePagedMails();
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.showError('Failed to load mails', `${this.getErrorMessage(err)}`);
      }
    });
  }

  /**
   * Deletes a mail by ID and removes it from the list.
   * @param mailId The ID of the mail to delete.
   */
  onDelete(mailId: string): void {
    this.mailService.deleteMail(mailId).subscribe({
      next: () => {
        this.mails = this.mails.filter(m => m.id !== mailId);
        this.updatePagedMails();
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.showError('Failed to delete mail', `${this.getErrorMessage(err)}`);
      }
    });
  }

  /**
   * Handles paginator page change events.
   * @param event The page change event.
   */
  onPageChange(event: PageEvent): void {
    this.pageIndex = event.pageIndex;
    this.pageSize = event.pageSize;
    this.updatePagedMails();
  }

  private showError(title: string, message: string): void {
    this.dialog.open(Dialog, {data: {title, message}});
  }

  private getErrorMessage(err: unknown): string {
    if (err && typeof err === 'object' && 'error' in err) {
      const error = (err as {error: {message?: string, errors?: string}}).error;
      if (error.message) return error.message;
      if (error.errors) return error.errors;
    }
    return 'An unexpected error occurred. Please try again.';
  }

  private updatePagedMails(): void {
    const start = this.pageIndex * this.pageSize;
    this.pagedMails = this.mails.slice(start, start + this.pageSize);
  }
}
