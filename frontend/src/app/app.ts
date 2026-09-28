import { Component, signal, inject } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Expense} from './expense'
import {DecimalPipe} from '@angular/common'
@Component({
  selector: 'app-root',
  styleUrl: './app.css',
  templateUrl: './app.html',
  imports:[DecimalPipe],
})
export class App {
  private readonly http = inject(HttpClient);

  protected readonly title = signal('Mis gastos');

  protected readonly expenses = signal<Expense[]>([]);


    loadExpenses() {
        this.http.get<Expense[]>('/api/expenses').subscribe((data) => {
          this.expenses.set(data);
          });
        }
    constructor(){
      this.loadExpenses();
      }
};
