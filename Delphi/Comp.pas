unit Comp;

{
The compressor class abstracts the algorithm used to compress the images.
PercentDone and CompressorRunning are called by the GUI updating thread to
monitor the progress of the algorithm.
}

interface

uses FractalObject, SFormList, FIM, RefImage, DestImage;

type
  TFractalCompressor = class(TFractalObject)
	  private
      mRegionSize: integer;
      mRefImage: TReferenceImage;
      mDestImage: TDestinationImage;
      mSFormList: TSFormList;
      mFIM: TFractalImageModel;
      mPercentDone: double;
      mCompressorRunning: boolean;
      procedure PrepareImages;
      procedure PrepareSForms;
    public
      property DestImage: TDestinationImage write mDestImage;
      property RegionSize: integer read mRegionSize write mRegionSize;
      property PercentDone: double read mPercentDone;
      property CompressorRunning: boolean read mCompressorRunning;
      property FractalImageModel: TFractalImageModel read mFIM;
      procedure Compress;
      constructor Create;
      destructor Destroy; override;
  end;

implementation

uses SForm, Metric, ImageRegion, FractalCode;

constructor TFractalCompressor.Create;
begin
  inherited Create;
  mRefImage := nil;
  mCompressorRunning := False;
  mPercentDone := 0;
  mSFormList := TSFormList.Create;
end;

destructor TFractalCompressor.Destroy;
begin
  mSFormList.Free;
  mRefImage.Free;
  inherited;
end;

// Sets up the ImageRegions in both images, creates the reference image.
procedure TFractalCompressor.PrepareImages;
begin
		mDestImage.PrepareDestinationRegions(mRegionSize);
		mRefImage := TReferenceImage.Create(mDestImage, 0.75);
		mRefImage.PrepareRefRegions(mRegionSize);
end;

// Sets up the SForms for the current region size.
procedure TFractalCompressor.PrepareSForms;
var
  i: integer;
begin
  for i := 0 to mSFormList.NumberOfSForms - 1 do
    mSFormList.GetSForm(i).Prepare(mRegionSize);
end;

// This does the actual compression.
// Preconditions: Panel Size and Dest Region are already set up.
procedure TFractalCompressor.Compress;
var
  error, beta, s, x, y, diff, bestError, bestBeta, bestSForm: integer;
  numDestRegions, numRefRegions, numDest, numRef: integer;
  destRegion, refRegion, bestRegion: TImageRegion;
  SForm: TSForm;
  Metric: TMetric;
begin
		mCompressorRunning := True;
    mPercentDone := 0;

		// Get an L2 metric
		Metric := TMetric.Create(2, mDestImage.MaxSize);
		bestSForm := 0; bestBeta := 0; bestRegion := nil;

		// initialization
		prepareImages();
		prepareSForms();
		numDestRegions := mDestImage.numberOfDestRegions;
		numRefRegions := mRefImage.numberOfRefRegions;
		mFIM := TFractalImageModel.Create(mDestImage.XRegions,
					mDestImage.YRegions, mDestImage.getDestRegion(0).RegionSize);

		// Loop over destination regions
		for numDest := 0 to numDestRegions - 1 do
    begin
			destRegion := mDestImage.getDestRegion(numDest);
			bestError := $7FFFFFFF;

			// Loop over reference regions
			for numRef := 0 to numRefRegions - 1 do
      begin
				refRegion := mRefImage.getRefRegion(numRef);

				// Difference in means
				beta := destRegion.Mean - refRegion.Mean;

				// Loop over SForms
				for s := 0 to mSFormList.NumberOfSForms - 1 do
        begin
					error := 0;
					SForm := mSFormList.getSForm(s);
					for x := 0 to mRegionSize - 1 do
						for y := 0 to mRegionSize - 1 do
            begin
							diff := destRegion.getPixel(x, y)
									-	SForm.getTransformedPixel(x, y, refRegion);
							error := error + Metric.getDistance(Abs(diff - beta));
						end;
					if(error < bestError) then
          begin
						bestError := error;
						bestSForm := s;
						bestBeta := beta;
						bestRegion := refRegion;
					end;
				end;  //  for s
				if(bestError = 0) then Break;
			end;  // for refRegion

			// Now we have the best Fractal Code for the destRegion.
      //frmMainForm.ListBox1.Items.Add('DestRegion: ' + IntToStr(numDest) + ': ' + IntToStr(bestRegion.X) + ' ' + IntToStr(bestRegion.Y) + ' ' + IntToStr(bestSForm) + ' ' + IntToStr(bestBeta) + ' ');
			mFIM.addFractalCode(TFractalCode.Create(bestRegion, bestSForm, bestBeta));
			mPercentDone := (100.0*(numDest + 1))/numDestRegions;
		end;  // for destRegion

		mCompressorRunning := False;
end;

end.
