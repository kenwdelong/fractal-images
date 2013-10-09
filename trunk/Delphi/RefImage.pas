unit RefImage;

// The reference image is used as the source of transformed pixels in both the
// compression and decompression cycles.  All of the SForms are chosen to have
// contractivity 2, and we choose the contrast factor gamma = 0.75.  So we
// apply these two transformations right off the bat, rather than repeatedly
// during the process.
interface

uses
  MonoImage, DestImage, ImageRegion;

type
  TReferenceImage = class(TMonochromeImage)
    private
      mRefRegions: PImageRegionArray;
    public
      procedure PrepareRefRegions(pRegionSize: integer);
      function NumberOfRefRegions: integer;
      function GetRefRegion(pI: integer): TImageRegion;
      constructor Create(pDestImage: TDestinationImage; pGamma: double);
      destructor Destroy; override;
  end;

implementation

// Creates the ref image from the dest image, applying the contractivity (2)
// and the gamma.
constructor TReferenceImage.Create(pDestImage: TDestinationImage; pGamma: double);
var
  val, xx, yy, x, y: integer;
begin
  mWidth := pDestImage.Width div 2;
  mHeight := pDestImage.Height div 2;
  GetMem(mPixels, mWidth*mHeight*SizeOf(byte));
  mRefRegions := nil;

  // The reference image gets shrunk by a factor of two, because
  // we are limiting ourselves to SForms with a contractivity of 2.
  // Also, we pre-multiply by the Gamma factor to save time.
  for x := 0 to mWidth - 1 do
    for y := 0 to mHeight - 1 do
    begin
      xx := 2*x; yy := 2*y;
      val := pDestImage.getPixel(xx, yy) + pDestImage.getPixel(xx + 1, yy) +
						pDestImage.getPixel(xx, yy + 1) + pDestImage.getPixel(xx + 1, yy + 1);
      mPixels^[x + mWidth*y] := byte(Trunc(((val*pGamma/4) + 0.49)));
    end;
end;

destructor TReferenceImage.Destroy;
var
  i: integer;
begin
  FreeMem(mPixels);
  if not (mRefRegions = nil) then
  begin
    for i := 0 to NumberOfRefRegions - 1 do mRefRegions^[i].Free;
    FreeMem(mRefRegions);
  end;
  inherited;
end;

// Sets up the reference image regions. The reference regions cover every
// possible set of pixels.
procedure TReferenceImage.PrepareRefRegions(pRegionSize: integer);
var
  numRegions, i, x, y: integer;
begin

  // Calculate the number of reference regions
		mXRegions := mWidth - (pRegionSize - 1);
		mYRegions := mHeight - (pRegionSize - 1);
		numRegions := mXRegions*mYRegions;
    GetMem(mRefRegions, numRegions*SizeOf(TImageRegion));

		// Create the reference regions.
    i := 0;
		for y := 0 to mYRegions - 1 do
			for x := 0 to mXRegions - 1 do
      begin
				mRefRegions^[i] := TImageRegion.Create(x, y, pRegionSize, Self);
        Inc(i);
      end;
end;

// Returns the total number of reference regions.
function TReferenceImage.NumberOfRefRegions: integer;
begin
  NumberOfRefRegions := mXRegions*mYRegions;
end;

function TReferenceImage.GetRefRegion(pI: integer): TImageRegion;
begin
  GetRefRegion := mRefRegions^[pI];
end;


end.
