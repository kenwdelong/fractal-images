unit MonoImage;

// The base class for all images.

interface

uses
  FractalObject;

type
  TByteArray = array[0..1048576] of byte;
  PByteArray = ^TByteArray;

  TMonochromeImage = class(TFractalObject)
    protected
      mWidth: integer;
      mHeight: integer;
      mPixels: PByteArray;
      mXRegions: integer;
      mYRegions: integer;
    public
      property Width: integer read mWidth;
      property Height: integer read mHeight;
      property XRegions: integer read mXRegions;
      property YRegions: integer read mYRegions;
      function GetPixel(pX, pY: integer): byte;
      procedure SetPixel(pX, pY: integer; pValue: byte);
      function GetPixels: PByteArray;
  end;

implementation

// Returns the value of a pixel.
function TMonochromeImage.GetPixel(pX, pY: integer): byte;
begin
  GetPixel := mPixels^[pX + pY*mWidth];
end;

// Sets the value of a pixel (not used now because we pass the pixel array out).
procedure TMonochromeImage.SetPixel(pX, pY: integer; pValue: byte);
begin
  mPixels^[pX + pY*mWidth] := pValue;
end;

// Passes out the pixel array, thus breaking encapsulation and speeding execution.
function TMonochromeImage.GetPixels: PByteArray;
begin
  GetPixels := mPixels;
end;

end.
