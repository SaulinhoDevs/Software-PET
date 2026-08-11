import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AgendamentoGrupo } from './agendamento-grupo';

describe('AgendamentoGrupo', () => {
  let component: AgendamentoGrupo;
  let fixture: ComponentFixture<AgendamentoGrupo>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AgendamentoGrupo]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AgendamentoGrupo);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
