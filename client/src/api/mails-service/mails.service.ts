import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable} from 'rxjs';
import {CreateMailRequest, MailFolder, MailResponse, UpdateMailRequest} from './mails.models';

@Injectable({providedIn: 'root'})
export class MailService {
  private readonly API_URL = 'http://localhost:8080/api/v1.0/mails';

  constructor(private http: HttpClient) {}

  getMails(folder: MailFolder): Observable<MailResponse[]> {
    const params = new HttpParams().set('folder', folder);
    return this.http.get<MailResponse[]>(this.API_URL, {params});
  }

  getMailById(mailId: string): Observable<MailResponse> {
    return this.http.get<MailResponse>(`${this.API_URL}/${mailId}`);
  }

  createMail(request: CreateMailRequest): Observable<MailResponse> {
    return this.http.post<MailResponse>(this.API_URL, request);
  }

  updateMail(mailId: string, request: UpdateMailRequest): Observable<MailResponse> {
    return this.http.put<MailResponse>(`${this.API_URL}/${mailId}`, request);
  }

  deleteMail(mailId: string): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/${mailId}`);
  }

  sendMail(mailId: string): Observable<MailResponse> {
    return this.http.post<MailResponse>(`${this.API_URL}/${mailId}/send`, {});
  }
}
