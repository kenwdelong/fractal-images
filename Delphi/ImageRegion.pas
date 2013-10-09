unit ImageRegion;

// The images are often partitioned into regions in the fractal algorithms.  This
// class abstracts the concept of an image region.  It contains the origin and
// size of the region, as well as the mean of the pixels that make it up.
interface

uses
  FractalObject, MonoImage;

type
  TImageRegion = class(TFractalObject)
    private
      mX: integer;
      mY: integer;
      mMean: integer;
      mRegionSize: integer;
      //mImage: TMonochromeImage;
      mPixels: PByteArray;
      mPixelsWidth: integer;
    public
      property X: integer read mX;
      property Y: integer read mY;
      property Mean: integer read mMean;
      property RegionSize: integer read mRegionSize;
      procedure SetPixel(pX, pY: integer; pValue: byte);
      function GetPixel(pX, pY: integer): byte;
      constructor Create(pX, pY, pRegionSize: integer; pImage: TMonochromeImage);
  end;

  TImageRegionArray = array[0..2097152] of TImageRegion;
  PImageRegionArray = ^TImageRegionArray;


implementation

uses Math;

// Constructor
constructor TImageRegion.Create(pX, pY, pRegionSize: integer; pImage: TMonochromeImage);
var
  x, y: integer;
begin
  inherited Create;
  mX := pX;
  mY := pY;
  mRegionSize := pRegionSize;
  // We hold the actual pixel array of the parent (thus breaking encapsulation) for speed's sake
  mPixels := pImage.GetPixels;
  mPixelsWidth := pImage.Width;

  // Find the mean
  mMean := 0;
  for x := 0 to mRegionSize - 1 do
    for y := 0 to mRegionSize - 1 do
      mMean := mMean + mPixels^[(pX + x) + mPixelsWidth*(pY + y)];
  mMean := Floor(mMean/(mRegionSize*mRegionSize) + 0.49);

end;

// Sets the value of a pixel in the parent image.
procedure TImageRegion.SetPixel(pX, pY: integer; pValue: byte);
begin
  mPixels^[(pX + mX) + mPixelsWidth*(pY + mY)] := pValue;
end;

// Gets the value of a pixel in the parent image.
function TImageRegion.GetPixel(pX, pY: integer): byte;
begin
  GetPixel := mPixels^[(pX + mX) + mPixelsWidth*(pY + mY)];
end;

end.
