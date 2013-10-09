unit Metric;

// represents the concept of a metric to use to measure the distance between
// two points.  Either an L1 or L2 metric (the default) can be selected.
interface

uses FractalObject;

type
  TIntegerArray = array[0..High(integer) div 8] of integer;
  PIntegerArray = ^TIntegerArray;

  TMetric = class(TFractalObject)
    private
      mMetricTable: PIntegerArray;
    public
      function GetDistance(pDiff: integer): integer;
      constructor Create(pL, pMax: integer);
      destructor Destroy; override;
  end;

implementation

uses Math;

// Constructs the metric table.  pMax is the maximum value of the pixels.
// The actual distance can be up to 2 times that.
constructor TMetric.Create(pL, pMax: integer);
var
  i, top: integer;
begin
  inherited Create;
  top := (pMax + 1)*2;
  GetMem(mMetricTable, top*SizeOf(integer));
  case pL of
    1:  for i := 0 to top - 1 do mMetricTable^[i] := i;
    else
      for i := 0 to top - 1 do mMetricTable^[i] := i*i;
  end;
end;

destructor TMetric.Destroy;
begin
  FreeMem(mMetricTable);
  inherited;
end;

// Returns the distance for the given difference between the two pixels.
function TMetric.GetDistance(pDiff: integer): integer;
begin
  GetDistance := mMetricTable^[pDiff];
end;

end.
