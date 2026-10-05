import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { App } from './app';

describe('App', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [App],
      providers: [provideHttpClientTesting()],
    })
      .compileComponents();
  });

  afterEach(() => {
    TestBed.inject(HttpTestingController).verify();
  });

  it('should render the title when the API returns no expenses', async () => {
    const fixture = TestBed.createComponent(App);
    const http = TestBed.inject(HttpTestingController);
    http
      .expectOne({ method: 'GET', url: '/api/expenses' })
      .flush([]);
    http.expectOne({ method: 'GET', url: '/api/expenses/total' }).flush(0);
    await fixture.whenStable();

    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('h1')?.textContent).toBe('Mis gastos');
    const paragraphs = compiled.querySelectorAll('p');
    expect(paragraphs.length).toBe(1);
    expect(paragraphs[0].textContent).toContain('Total general: 0.00 €');
  });

  it('should render expenses received from the API', async () => {
    const fixture = TestBed.createComponent(App);
    const http = TestBed.inject(HttpTestingController);
    http
      .expectOne({ method: 'GET', url: '/api/expenses' })
      .flush([
        {
          id: 205,
          description: 'Billete de autobus',
          amount: 3.20,
          category: 'TRANSPORT',
          date: '2026-09-24'
        },
        {
          id: 206,
          description: 'Comida',
          amount: 12.50,
          category: 'FOOD',
          date: '2026-09-25'
        }
      ]);
    http.expectOne({ method: 'GET', url: '/api/expenses/total' }).flush(15.70);
    await fixture.whenStable();

    const compiled = fixture.nativeElement as HTMLElement;
    const paragraphs = compiled.querySelectorAll('p');
    expect(paragraphs.length).toBe(3);
    expect(paragraphs[0].textContent).toContain('Total general: 15.70 €');
    expect(paragraphs[1].textContent).toContain('Billete de autobus');
    expect(paragraphs[1].textContent).toContain('3.20');
    expect(paragraphs[1].textContent).toContain('TRANSPORT');
    expect(paragraphs[2].textContent).toContain('Comida');
    expect(paragraphs[2].textContent).toContain('12.50');
    expect(paragraphs[2].textContent).toContain('FOOD');
  });

  it('should edit an expense and refresh the list and total', async () => {
    const fixture = TestBed.createComponent(App);
    const http = TestBed.inject(HttpTestingController);
    const expense = {
      id: 205,
      description: 'Billete de autobus',
      amount: 3.20,
      category: 'TRANSPORT',
      date: '2026-09-24',
    };
    http.expectOne({ method: 'GET', url: '/api/expenses' }).flush([expense]);
    http.expectOne({ method: 'GET', url: '/api/expenses/total' }).flush(3.20);
    await fixture.whenStable();

    const compiled = fixture.nativeElement as HTMLElement;
    const editButton = Array.from(compiled.querySelectorAll('button'))
      .find(button => button.textContent?.trim() === 'Editar');
    expect(editButton).toBeDefined();
    editButton!.click();
    await fixture.whenStable();

    const editForm = compiled.querySelectorAll('form')[1] as HTMLFormElement;
    expect(editForm).toBeDefined();
    expect((editForm.elements.namedItem('description') as HTMLInputElement).value)
      .toBe('Billete de autobus');
    expect((editForm.elements.namedItem('amount') as HTMLInputElement).value).toBe('3.2');
    expect((editForm.elements.namedItem('category') as HTMLSelectElement).value)
      .toBe('TRANSPORT');
    expect((editForm.elements.namedItem('date') as HTMLInputElement).value)
      .toBe('2026-09-24');

    (editForm.elements.namedItem('description') as HTMLInputElement).value = 'Billete de tren';
    editForm.dispatchEvent(new Event('submit', { bubbles: true, cancelable: true }));
    const update = http.expectOne({ method: 'PUT', url: '/api/expenses/205' });
    expect(update.request.body).toEqual({
      description: 'Billete de tren',
      amount: 3.2,
      category: 'TRANSPORT',
      date: '2026-09-24',
    });
    update.flush({ ...expense, description: 'Billete de tren' });
    http.expectOne({ method: 'GET', url: '/api/expenses' })
      .flush([{ ...expense, description: 'Billete de tren' }]);
    http.expectOne({ method: 'GET', url: '/api/expenses/total' }).flush(3.20);
    await fixture.whenStable();

    expect(compiled.textContent).toContain('Billete de tren');
    expect(compiled.querySelectorAll('form').length).toBe(1);
  });
});
