unit CompThread;

// The thread that runs the compressor.

interface

uses
  Classes, Comp;

type
  TCompThread = class(TThread)
  private
    { Private declarations }
    mCompressor: TFractalCompressor;
  protected
    procedure Execute; override;
  public
    constructor Create(pCompressor: TFractalCompressor);
  end;

implementation

{ Important: Methods and properties of objects in VCL can only be used in a
  method called using Synchronize, for example,

      Synchronize(UpdateCaption);

  and UpdateCaption could look like,

    procedure CompThread.UpdateCaption;
    begin
      Form1.Caption := 'Updated in a thread';
    end; }

{ CompThread }

constructor TCompThread.Create(pCompressor: TFractalCompressor);
begin
  inherited Create(false);
  mCompressor := pCompressor;
  FreeOnTerminate := true;
end;

procedure TCompThread.Execute;
begin
  { Place thread code here }
  mCompressor.Compress;
end;

end.
