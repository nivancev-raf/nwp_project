import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OrderScheduleComponent } from './order-schedule.component';

describe('OrderScheduleComponent', () => {
  let component: OrderScheduleComponent;
  let fixture: ComponentFixture<OrderScheduleComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OrderScheduleComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(OrderScheduleComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
