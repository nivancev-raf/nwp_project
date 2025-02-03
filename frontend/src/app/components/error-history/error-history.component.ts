import { Component, OnInit } from '@angular/core';
import { ErrorMessage } from '../../models/error-message';
import { ErrorService } from '../../services/error.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-error-history',
  templateUrl: './error-history.component.html',
  imports: [CommonModule],
  standalone: true
})
export class ErrorHistoryComponent implements OnInit {

  ngOnInit(): void {
    this.loadErrors();
  }

  errors: ErrorMessage[] = [];
  currentPage = 0;
  pageSize = 10;
  totalElements = 0;
  pages: number[] = [];

  constructor(private errorService: ErrorService) {}

  async loadErrors() {
    const result = await this.errorService.getErrors(this.currentPage, this.pageSize);
    console.log('result', result);
    this.errors = result.content;
    this.totalElements = result.totalElements;
    this.calculatePages();
  }

  private calculatePages() {
    const pageCount = Math.ceil(this.totalElements / this.pageSize);
    this.pages = Array.from({length: pageCount}, (_, i) => i);
  }

  onPageChange(page: number) {
    this.currentPage = page;
    this.loadErrors();
  }
}