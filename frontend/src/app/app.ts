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

  protected readonly errorMessage = signal('');

  protected readonly selectedCategory = signal('');

  protected readonly total = signal(0);

  protected readonly editingExpense = signal<Expense | null>(null);

    onFilterChange(event: Event): void {
      const select = event.target as HTMLSelectElement;
      this.selectedCategory.set(select.value);
      this.loadExpenses();

      }
    loadExpenses() {
      const category = this.selectedCategory();
      let url = '/api/expenses';

       if(category){
         url = `/api/expenses?category=${category}`;

         }
       this.http.get<Expense[]>(url).subscribe((data) => {
           this.expenses.set(data);
          });
        }
      loadTotal(): void{
        this.http.get<number>('/api/expenses/total').subscribe((value) => {
          this.total.set(value);
          });
        }
    constructor(){
      this.loadExpenses();
      this.loadTotal();
      }
    onSubmit(event: Event): void{
      event.preventDefault();
      const form = event.target as HTMLFormElement;
      const values = new FormData(form);

      const request = {
        description: values.get('description'),
        amount: Number(values.get('amount')),
        category:values.get('category'),
        date:values.get('date'),}

      this.http.post<Expense>('/api/expenses', request).subscribe({
        next: (savedExpense) => {
          console.log(savedExpense);
          this.loadExpenses();
          this.errorMessage.set('')
          this.loadTotal();
          form.reset();


          },
        error: (err) => {
          console.error(err.status);
          this.errorMessage.set('No se pudo guardar el gasto')
          }
        });
      };
    deleteExpense(id:number): void {
      this.http.delete<void>(`/api/expenses/${id}`).subscribe({
        next: () =>{
            this.loadExpenses();
            this.loadTotal();
          },

        error: () =>{
          this.errorMessage.set('No se pudo eliminar el gasto')
          },

        });
      }
    startEditing(expense: Expense): void{
      this.editingExpense.set(expense)
      this.errorMessage.set('')
      }

    cancelEditing(): void {
      this.editingExpense.set(null);
      this.errorMessage.set('');
    }

    onEditSubmit(event: Event, id: number): void {
      event.preventDefault();
      const form = event.target as HTMLFormElement;
      const values = new FormData(form);
      const request = {
        description: values.get('description'),
        amount: Number(values.get('amount')),
        category: values.get('category'),
        date: values.get('date'),
      };

      this.http.put<Expense>(`/api/expenses/${id}`, request).subscribe({
        next: () => {
          this.editingExpense.set(null);
          this.errorMessage.set('');
          this.loadExpenses();
          this.loadTotal();
        },
        error: () => {
          this.errorMessage.set('No se pudo actualizar el gasto');
        },
      });
    }

};
