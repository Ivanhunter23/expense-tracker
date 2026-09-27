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
    TestBed.inject(HttpTestingController)
      .expectOne({ method: 'GET', url: '/api/expenses' })
      .flush([]);
    await fixture.whenStable();

    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('h1')?.textContent).toBe('Mis gastos');
    expect(compiled.querySelectorAll('p').length).toBe(0);
  });

  it('should render expenses received from the API', async () => {
    const fixture = TestBed.createComponent(App);
    TestBed.inject(HttpTestingController)
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
    await fixture.whenStable();

    const compiled = fixture.nativeElement as HTMLElement;
    const rows = compiled.querySelectorAll('p');
    expect(rows.length).toBe(2);
    expect(rows[0].textContent).toContain('Billete de autobus');
    expect(rows[0].textContent).toContain('3.2');
    expect(rows[0].textContent).toContain('TRANSPORT');
    expect(rows[1].textContent).toContain('Comida');
    expect(rows[1].textContent).toContain('12.5');
    expect(rows[1].textContent).toContain('FOOD');
  });
});
