unit FractalCode;

// These are the transformations that represent the image.  There is a one-to-one
// mapping between destination regions and fractal codes.
interface

uses FractalObject, SFormList, ImageRegion, SForm;

type
  TFractalCode = class(TFractalObject)
    private
      mX: integer;
      mY: integer;
      mS: integer;
      mBeta: integer;
      //mSFormList: TSFormList;
    public
      property X: integer read mX;
      property Y: integer read mY;
      property S: integer read mS;
      property Beta: integer read mBeta;
      function GetSForm: TSForm;
      constructor Create(pRefRegion: TImageRegion; pS, pBeta: integer);
      constructor CreateFromList(pX, pY, pS, pBeta: integer);
      destructor Destroy; override;
  end;

  TFractalCodeArray = array[0..1048576] of TFractalCode;
  PFractalCodeArray = ^TFractalCodeArray;

implementation

// Create the code.
constructor TFractalCode.Create(pRefRegion: TImageRegion; pS, pBeta: integer);
begin
  inherited Create;
  mX := pRefRegion.X;
  mY := pRefRegion.Y;
  mS := pS;
  mBeta := pBeta;
  //mSFormList := nil;
end;

// Used when reading in the FIM from disk.
constructor TFractalCode.CreateFromList(pX, pY, pS, pBeta: integer);
begin
  inherited Create;
  mX := pX;
  mY := pY;
  mS := pS;
  mBeta := pBeta;
  //mSFormList := nil;
end;

destructor TFractalCode.Destroy;
begin
  //mSFormList.Free;
  inherited;
end;

// Returns the SForm.
function TFractalCode.GetSForm: TSForm;
begin
  {if mSFormList = nil then
    mSFormList := TSFormList.Create;
  GetSForm := mSFormList.GetSForm(mS);}
  if GlobalSFormList = nil then
    GlobalSFormList := TSFormList.Create;
  GetSForm := GlobalSFormList.GetSForm(mS);
end;

end.
