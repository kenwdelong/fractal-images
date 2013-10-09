program FractalImages;

uses
  Forms,
  MainForm in 'MainForm.pas' {frmMainForm},
  Checker in 'Checker.pas',
  Comp in 'Comp.pas',
  CompThread in 'CompThread.pas',
  Decomp in 'Decomp.pas',
  DestImage in 'DestImage.pas',
  FIM in 'FIM.pas',
  FractalCode in 'FractalCode.pas',
  FractalObject in 'FractalObject.pas',
  ImageForm in 'ImageForm.pas' {frmImage},
  ImageRegion in 'ImageRegion.pas',
  Metric in 'Metric.pas',
  MonoImage in 'MonoImage.pas',
  RefImage in 'RefImage.pas',
  SForm in 'SForm.pas',
  SFormList in 'SFormList.pas';

{$R *.RES}

begin
  Application.Initialize;
  Application.CreateForm(TfrmMainForm, frmMainForm);
  Application.Run;
end.
