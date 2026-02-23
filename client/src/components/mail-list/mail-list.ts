import {Component, OnInit} from '@angular/core';
import {MatNavList} from '@angular/material/list';
import {MatPaginator, PageEvent} from '@angular/material/paginator';
import {User} from '../../api/auth-service/auth.models';
import {AuthService} from '../../api/auth-service/auth.service';
import {MatButton} from '@angular/material/button';
import {MailListItem} from '../mail-list-item/mail-list-item';
import {NgForOf} from '@angular/common';
import {Mail} from '../../types/mailType';

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
export class MailList implements OnInit{
  mails = [
    {
      id: "1",
      subject: "Team Sync Tomorrow",
      sender: "alice@company.com",
      content: "Reminder that we have our weekly team sync tomorrow at 10 AM.",
      isExternal: false
    },
    {
      id: "2",
      subject: "Invoice for August",
      sender: "billing@vendor.com",
      content: "Please find attached the invoice for services provided in August.",
      isExternal: true
    },
    {
      id: "3",
      subject: "Password Reset Request",
      sender: "no-reply@company.com",
      content: "Click the link below to reset your password.",
      isExternal: false
    },
    {
      id: "4",
      subject: "Lunch Plans?",
      sender: "bob@company.com",
      content: "Are you free for lunch today around noon?",
      isExternal: false
    },
    {
      id: "5",
      subject: "New Feature Announcement",
      sender: "news@saasplatform.com",
      content: "We’re excited to announce a new feature launching next week!",
      isExternal: true
    },
    {
      id: "6",
      subject: "Meeting Notes",
      sender: "carol@company.com",
      content: "I’ve shared the notes from today’s meeting in the doc.",
      isExternal: false
    },
    {
      id: "7",
      subject: "Security Alert",
      sender: "alerts@securityservice.com",
      content: "We detected a new login to your account from an unknown device.",
      isExternal: true
    },
    {
      id: "8",
      subject: "Code Review Feedback",
      sender: "dave@company.com",
      content: "Left a few comments on your PR—overall looks good!",
      isExternal: false
    },
    {
      id: "9",
      subject: "Conference Invitation",
      sender: "events@techconf.io",
      content: "You’re invited to speak at our upcoming tech conference.",
      isExternal: true
    },
    {
      id: "10",
      subject: "Welcome to the Team!",
      sender: "hr@company.com",
      content: "We’re excited to have you onboard. Let us know if you need anything.",
      isExternal: false
    }
  ];
  user: User | null = null;
  pagedMails: Mail[] = [];   // what we actually render

  pageSize = 5;
  pageIndex = 0;

  onPageChange(event: PageEvent) {
    this.pageIndex = event.pageIndex;
    this.pageSize = event.pageSize;
    this.updatePagedMails();
  }

  private updatePagedMails() {
    const startIndex = this.pageIndex * this.pageSize;
    const endIndex = startIndex + this.pageSize;
    this.pagedMails = this.mails.slice(startIndex, endIndex);
  }

  constructor(private authService: AuthService) {}

  ngOnInit() {
    this.authService.currentUser$.subscribe(user => {
      this.user = user;
    });
    this.updatePagedMails();
  }
}
