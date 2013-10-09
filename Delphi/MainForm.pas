unit MainForm;

interface

uses
  Windows, Messages, SysUtils, Classes, Graphics, Controls, Forms, Dialogs,
  StdCtrls, ExtDlgs, ImageForm, Comp, FIM;

type
  TfrmMainForm = class(TForm)
    cmdSelectFile: TButton;
    cmdCompress: TButton;
    lblCompFileSize: TLabel;
    cmdHalt: TButton;
    lblPercentDone: TLabel;
    lblTimeLeft: TLabel;
    cbxCompRegionSize: TComboBox;
    cmdSelectFIM: TButton;
    cbxDecompRegionSize: TComboBox;
    cmdDecompress: TButton;
    lblDecompSize: TLabel;
    lblCompRegionSize: TLabel;
    lblIterations: TLabel;
    OpenDialog1: TOpenDialog;
    SaveDialog1: TSaveDialog;
    OpenPictureDialog1: TOpenPictureDialog;
    procedure cmdSelectFileClick(Sender: TObject);
    procedure FormCreate(Sender: TObject);
    procedure cmdCompressClick(Sender: TObject);
    procedure cmdSelectFIMClick(Sender: TObject);
    procedure cmdDecompressClick(Sender: TObject);
    procedure cmdHaltClick(Sender: TObject);
  private
    { Private declarations }
    mFrmCompImage: TfrmImage;
    mFIM: TFractalImageModel;
  public
    { Public declarations }
    Compressor: TFractalCompressor;
  end;

var
  frmMainForm: TfrmMainForm;

implementation

uses DestImage, Checker, CompThread, DeComp;

var
  checkerThread: TChecker;
  compressorThread: TCompThread;

{$R *.DFM}

procedure TfrmMainForm.cmdSelectFileClick(Sender: TObject);
var
  imageHeight, imageWidth: integer;
  CompressFileName: string;
begin
  if OpenPictureDialog1.Execute then
  begin
    CompressFileName := OpenPictureDialog1.Filename;
    mFrmCompImage := TfrmImage.Create(Self);
    mFrmCompImage.Caption := 'Image to Compress.';
    mFrmCompImage.imgImage.Picture.LoadFromFile(CompressFileName);
    imageHeight := mFrmCompImage.imgImage.Picture.Height;
    imageWidth := mFrmCompImage.imgImage.Picture.Width;
    lblCompFileSize.Caption := 'Image size: ' + IntToStr(imageWidth) + ' X '
      + IntToStr(imageHeight);
    mFrmCompImage.Height := imageHeight + 40;
    mFrmCompImage.Width := imageWidth + 20;
    mFrmCompImage.Show;
    cmdCompress.Enabled := True;
  end;
end;

procedure TfrmMainForm.FormCreate(Sender: TObject);
begin
  cbxCompRegionSize.ItemIndex := 3;
  cbxDecompRegionSize.ItemIndex := 3;
  mFIM := nil;
end;

procedure TfrmMainForm.cmdCompressClick(Sender: TObject);
var
  destImage: TDestinationImage;
  regionSize: integer;
begin 
  compressor := TFractalCompressor.Create;
  destImage := TDestinationImage.Create(mFrmCompImage.imgImage);
  regionSize := StrToInt(cbxCompRegionSize.Items[cbxCompRegionSize.ItemIndex]);

  compressor.RegionSize := regionSize;
  compressor.DestImage := destImage;

  compressorThread := TCompThread.Create(compressor);
  checkerThread := TChecker.Create(Self);

  cmdCompress.Enabled := False;
  cmdHalt.Enabled := True;
end;

procedure TfrmMainForm.cmdSelectFIMClick(Sender: TObject);
begin
  OpenDialog1.Title := 'Load Fractal Image Model';
  if OpenDialog1.Execute then
  begin
    mFIM.Free;
    mFIM := TFractalImageModel.CreateNull;
    mFIM.LoadFromFile(OpenDialog1.FileName);
    cmdDecompress.Enabled := True;
  end;
end;

procedure TfrmMainForm.cmdDecompressClick(Sender: TObject);
var
  destImage: TDestinationImage;
  regionSize, Iwidth, Iheight, i: integer;
  decomp: TFractalDecompressor;
  frmRecon: TfrmImage;
begin
  decomp := TFractalDecompressor.Create;
  regionSize := StrToInt(cbxDecompRegionSize.Items[cbxDecompRegionSize.ItemIndex]);

  Iwidth := regionSize*mFIM.XRegions;
  Iheight := regionSize*mFIM.YRegions;
  destImage := TDestinationImage.CreateGray(Iwidth, Iheight);
  destImage.PrepareDestinationRegions(regionSize);

  lblDecompSize.Caption := 'ImageSize: ' + IntToStr(IWidth) + ' X ' + IntToStr(Iheight);
  lblDecompSize.Refresh;
  Refresh;

  frmRecon := TfrmImage.Create(Self);
  frmRecon.imgImage.Width := Iwidth;
  frmRecon.imgImage.Height := Iheight;
  frmRecon.Width := Iwidth + 20;
  frmRecon.Height := Iheight + 40;
  frmRecon.Left := 300;
  frmRecon.Caption := 'Decompressed Image';
  destImage.PaintImage(frmRecon.imgImage);
  frmRecon.Show;

  for i := 0 to 15 do
  begin
    decomp.GetNextImage(mFIM, destImage, regionSize);
    frmRecon.imgImage.Visible := False;
    destImage.PaintImage(frmRecon.imgImage);
    frmRecon.imgImage.Visible := True;
    lblIterations.Caption := 'Decomp. Iterations: ' + IntToStr(i + 1);
    frmRecon.imgImage.Refresh;
    lblIterations.Refresh;
  end;

  destImage.Free;
  decomp.Free;
end;

procedure TfrmMainForm.cmdHaltClick(Sender: TObject);
begin
  checkerThread.Terminate;
  // The compressor thread doesn't watch for Terminated, and I didn't want to
  // build this into TFractalDecompressor.
  compressorThread.Suspend;
  compressorThread.Destroy;  // Don't know if this will work or not.

  cmdCompress.Enabled := True;
  cmdHalt.Enabled := False;
end;

end.
