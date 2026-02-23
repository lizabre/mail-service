import {Component, Input, OnInit} from '@angular/core';
import {MatNavList} from '@angular/material/list';
import {MatPaginator, PageEvent} from '@angular/material/paginator';
import {User} from '../../api/auth-service/auth.models';
import {AuthService} from '../../api/auth-service/auth.service';
import {MatButton} from '@angular/material/button';
import {MailListItem} from '../mail-list-item/mail-list-item';
import {NgForOf} from '@angular/common';
import {MailService} from '../../api/mails-service/mails.service';
import {MailResponse} from '../../api/mails-service/mails.models';

@Component({
  selector: 'app-mail-list',
  imports: [
    MatNavList,
    MatPaginator,
    MatButton,
    MailListItem,
    NgForOf,
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
    private mailService: MailService
  ) {}

  ngOnInit(): void {
    this.authService.currentUser$.subscribe(user => {
      this.user = user;
    });

    this.loadMails();
  }

  loadMails(): void {
    this.mailService.getMails(this.folder).subscribe({

      next: (data) => {
        console.log(data)
        this.mails = data;
        this.updatePagedMails();
      },
      error: (err) => console.error('Failed to load mails', err)
    });
  }

  onDelete(mailId: string): void {
    this.mailService.deleteMail(mailId).subscribe({
      next: () => {
        this.mails = this.mails.filter(m => m.id !== mailId);
        this.updatePagedMails();
      },
      error: (err) => console.error('Failed to delete mail', err)
    });
  }

  onPageChange(event: PageEvent): void {
    this.pageIndex = event.pageIndex;
    this.pageSize = event.pageSize;
    this.updatePagedMails();
  }

  private updatePagedMails(): void {
    const start = this.pageIndex * this.pageSize;
    this.pagedMails = this.mails.slice(start, start + this.pageSize);
  }
}
