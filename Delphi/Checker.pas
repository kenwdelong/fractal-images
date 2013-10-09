unit Checker;

interface

uses
  Classes, MainForm;

type
  TChecker = class(TThread)
  private
    { Private declarations }
    mMainForm: TfrmMainForm;
    mPercentString: string;
    mTimeString: string;
    procedure UpdateDisplay;
    procedure SaveTheFIM;
  protected
    procedure Execute; override;
  public
    constructor Create(frmMain: TfrmMainForm);
  end;

implementation

uses Comp, SysUtils, Math, FIM, Windows;

{ Important: Methods and properties of objects in VCL can only be used in a
  method called using Synchronize, for example,

      Synchronize(UpdateCaption);

  and UpdateCaption could look like,

    procedure TChecker.UpdateCaption;
    begin
      Form1.Caption := 'Updated in a thread';
    end; }

{ TChecker }

constructor TChecker.Create(frmMain: TfrmMainForm);
begin
  inherited Create(false);
  mMainForm := frmMain;
  Priority := tpLowest;
  FreeOnTerminate := True;
end;

procedure TChecker.Execute;
var
  percentDone, elapsedTime, projectedTime: double;
  startTime, nowTime: TDateTime;
  minutes, seconds: integer;
begin
  { Place thread code here }

		startTime := Now;
		repeat
      Sleep(3000);
      if Terminated then Exit;

			percentDone := mMainForm.Compressor.PercentDone;
      mPercentString := 'Percent Done: ' + FloatToStrF(percentDone, ffGeneral, 4, 4);
			if(percentDone <> 0.0) then
      begin
				nowTime := Now;
				elapsedTime := (nowTime - startTime)*24*60;  // in minutes
				projectedTime := ((100/percentDone - 1)*elapsedTime);
				minutes := Floor(projectedTime);
				seconds := Floor((projectedTime - minutes)*60);
				mTimeString := 'Time Remaining: ' + IntToStr(minutes) + ' min ' + IntToStr(seconds) + ' sec.';
        Synchronize(UpdateDisplay);
			end;
		 until (not mMainForm.Compressor.compressorRunning);

		// If we get here, that means the compressor finished normally.
		// Now save the image model.
    Synchronize(SaveTheFIM);
end;

procedure TChecker.UpdateDisplay;
begin
  mMainForm.lblPercentDone.Caption := mPercentString;
  mMainForm.lblTimeLeft.Caption := mTimeString;
end;

procedure TChecker.SaveTheFIM;
begin
  mMainForm.SaveDialog1.Title := 'Save Fractal Image Model to File:';
  if mMainForm.SaveDialog1.Execute then
  begin
    mMainForm.Compressor.FractalImageModel.SaveToFile(mMainForm.SaveDialog1.Filename);
  end;
end;

end.
