unit FIM;

// The fractal image model is the description of the image in terms of the
// fractal codes discovered by the compressor.

interface

uses FractalObject, FractalCode;

type
  TFractalImageModel = class(TFractalObject)
    private
      mXRegions: integer;
      mYRegions: integer;
      mSize: integer;
      mRegionSize: integer;
      mCodes: PFractalCodeArray;
    public
      property XRegions: integer read mXRegions;
      property YRegions: integer read mYRegions;
      property Size: integer read mSize;
      property RegionSize: integer read mRegionSize;
      function Capacity: integer;
      procedure AddFractalCode(pFractalCode: TFractalCode);
      function GetFractalCode(pI: integer): TFractalCode;
      procedure SaveToFile(pFilename: string);
      procedure LoadFromFile(pFilename: string);
      constructor Create(pXRegions, pYRegions, pRegionSize: integer);
      constructor CreateNull;
      destructor Destroy; override;

  end;

implementation

uses Classes, SysUtils;

// Use this one when reading in from disk
constructor TFractalImageModel.CreateNull;
begin
  inherited Create;
end;

// Creates a FIM with the appropriate parameters
constructor TFractalImageModel.Create(pXRegions, pYRegions, pRegionSize: integer);
begin
  inherited Create;
  mXRegions := pXRegions;
  mYRegions := pYRegions;
  mRegionSize := pRegionSize;
  mSize := 0;
  GetMem(mCodes, Capacity*SizeOf(TFractalCode));
end;

destructor TFractalImageModel.Destroy;
var
  i: integer;
begin
  for i := 0 to Size - 1 do
  begin
    mCodes^[i].Free;
  end;

  FreeMem(mCodes);
  inherited;
end;

// The total number of fractal codes that the FIM can hold.
function TFractalImageModel.Capacity: integer;
begin
  Capacity := mXRegions*mYRegions;
end;

// Adds a fractal code to the list.
procedure TFractalImageModel.AddFractalCode(pFractalCode: TFractalCode);
begin
  mCodes^[mSize] := pFractalCode;
  Inc(mSize);
end;

// Returns the specified fractal code.
function TFractalImageModel.GetFractalCode(pI: integer): TFractalCode;
begin
  GetFractalCode := mCodes^[pI];
end;

// Saves the FIM to file.  This is not a particularly efficient encoding
// (in fact it stinks), but it's easy.  A better way would be to use the
// same integer encoding but save it as binary.  Or better yet, pack the
// bits better (beta only needs 8 bits, and s only 3).
procedure TFractalImageModel.SaveToFile(pFilename: string);
var
  position, sAndBeta, i: integer;
  list: TStringList;
begin
  list := TStringList.Create;
  list.Add(IntToStr(mXRegions));
  list.Add(IntToStr(mYRegions));
  list.Add(IntToStr(mSize));
  list.Add(IntToStr(mRegionSize));

  for i := 0 to Size - 1 do
  begin
    position := ((mCodes^[i].X) shl 16) + mCodes^[i].Y;
    sAndBeta := ((mCodes^[i].Beta) shl 16) + mCodes^[i].S;
    list.Add(IntToStr(position));
    list.Add(intToStr(sAndBeta));
  end;

  list.SaveToFile(pFilename);
  list.Free;
end;

// Read the fractal code in from disk.
procedure TFractalImageModel.LoadFromFile(pFilename: string);
var
  position, sAndBeta, i, x, y, s, beta: integer;
  list: TStringList;
begin
  list := TStringList.Create;
  list.LoadFromFile(pFilename);

  mXRegions := StrToInt(list[0]);
  mYRegions := StrToInt(list[1]);
  mSize := StrToInt(list[2]);
  mRegionSize := StrToInt(list[3]);

  //FreeMem(mCodes);
  GetMem(mCodes, Capacity*SizeOf(TFractalCode));

  for i := 0 to Size - 1 do
  begin
    position := StrToInt(list[2*i + 4]);
    sAndBeta := StrToInt(list[2*i + 5]);
    x := (position and $FFFF0000) shr 16;
    y := position and $0000FFFF;
    s := sAndBeta and $0000FFFF;
    beta := smallint((sAndBeta and $FFFF0000) shr 16);
    mCodes^[i] := TFractalCode.CreateFromList(x, y, s, beta);
  end;

  list.Free;
end;

end.
