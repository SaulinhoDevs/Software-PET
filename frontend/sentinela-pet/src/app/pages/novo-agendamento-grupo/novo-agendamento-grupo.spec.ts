import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NovoAgendamentoGrupo } from './novo-agendamento-grupo';

describe('NovoAgendamentoGrupo', () => {
  let component: NovoAgendamentoGrupo;
  let fixture: ComponentFixture<NovoAgendamentoGrupo>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NovoAgendamentoGrupo]
    })
    .compileComponents();

    fixture = TestBed.createComponent(NovoAgendamentoGrupo);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
