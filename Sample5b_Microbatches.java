// =============================================================================
// Approximation the spiral-like function using the micro-batch method.
// The input is the normalized set of micro-batch files  (each micro-batch
// includes a single day record). 
// Each record consists of: 
// - normDayValue
// - normTargetValue
//
// The number of inputLayer neurons is 1
// The number of outputLayer neurons is 1
//
// Each network is saved on disk and a map is created to link each saved trained 
// network with the corresponding training micro-batch file.
// =============================================================================

package sample5b_microbatches;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.Properties;
import java.time.YearMonth;
import java.awt.Color;
import java.awt.Font;
import java.io.BufferedReader;
import java.time.Month;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import org.encog.Encog;
import org.encog.engine.network.activation.ActivationTANH;
import org.encog.engine.network.activation.ActivationReLU;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.buffer.MemoryDataLoader;
import org.encog.ml.data.buffer.codec.CSVDataCODEC;
import org.encog.ml.data.buffer.codec.DataSetCODEC;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.propagation.resilient.
  ResilientPropagation;
import org.encog.persist.EncogDirectoryPersistence;
import org.encog.util.csv.CSVFormat;

import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.demo.charts.ExampleChart;
import org.knowm.xchart.style.Styler.LegendPosition;
import org.knowm.xchart.style.colors.ChartColor;
import org.knowm.xchart.style.colors.XChartSeriesColors;
import org.knowm.xchart.style.lines.SeriesLines;
import org.knowm.xchart.style.markers.SeriesMarkers;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.BitmapEncoder.BitmapFormat;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;

public class Sample5b_Microbatches implements ExampleChart<XYChart>
{
   // Normalization parameters 
    
   // Normalizing interval  
   static double Nh =  1;  
   static double Nl = -1;
      
   // inputFunctValueDiffPerc
static double inputDayDh = 20.00; 
static double inputDayDl = 1.00;
   
// targetFunctValueDiffPerc 
static double targetFunctValueDiffPercDh = 20.00; 
static double targetFunctValueDiffPercDl = 1.00;
   
   static String cvsSplitBy = ",";
   static Properties prop = null;
      
   static String strWorkingMode;
   static String strNumberOfBatchesToProcess;
   static String strTrainFileNameBase;
   static String strTestFileNameBase;
   static String strSaveTrainNetworkFileBase;
   static String strSaveTestNetworkFileBase; 
   static String strValidateFileName;
   static String strTrainChartFileName;
   static String strTestChartFileName;
   static String strFunctValueTrainFile; 
   static String strFunctValueTestFile; 
   static int intDayNumber;
   static double doubleDayNumber;
   static int intWorkingMode;
   static int numberOfTrainBatchesToProcess;
   static int numberOfTestBatchesToProcess;
   static int intNumberOfRecordsInTrainFile; 
   static int intNumberOfRecordsInTestFile; 
   static int intNumberOfRowsInBatches; 
   static int intInputNeuronNumber;
   static int intOutputNeuronNumber;
   static String strOutputFileName;
   static String strSaveNetworkFileName;
   static String strDaysTrainFileName;
   static XYChart Chart;
   static String iString;
   static double inputFunctValueFromFile;
   static double targetToPredictFunctValueDiff;
   static int[] returnCodes  = new int[3];
   
   static List<Double> xData = new ArrayList<Double>();
   static List<Double> yData1 = new ArrayList<Double>();   
   static List<Double> yData2 = new ArrayList<Double>(); 
   
   static double[] DaysyearDayTraining = new double[1200];
   static String[] strTrainingFileNames = new String[1200]; 
   static String[] strTestingFileNames = new String[1200]; 
   static String[] strSaveTrainNetworkFileNames = new String[1200];
   static double[] linkToSaveNetworkDayKeys = new double[1200];
   static double[] linkToSaveNetworkTargetFunctValueKeys = new double[1200];
   static double[] arrTrainFunctValues = new double[1200]; 
   static double[] arrTestFunctValues = new double[1200]; 
   
  @Override
  public XYChart getChart()
   {
     // Create Chart
    
    Chart = new XYChartBuilder().width(900).height(500).title(getClass().
      getSimpleName()).xAxisTitle("day").yAxisTitle("y=f(day)").build();
    
    // Customize Chart
    Chart.getStyler().setPlotBackgroundColor(ChartColor.getAWTColor(ChartColor.GREY));
    Chart.getStyler().setPlotGridLinesColor(new Color(255, 255, 255));
    Chart.getStyler().setChartBackgroundColor(Color.WHITE);
    Chart.getStyler().setLegendBackgroundColor(Color.PINK);
    Chart.getStyler().setChartFontColor(Color.MAGENTA);
    Chart.getStyler().setChartTitleBoxBackgroundColor(new Color(0, 222, 0));
    Chart.getStyler().setChartTitleBoxVisible(true);
    Chart.getStyler().setChartTitleBoxBorderColor(Color.BLACK);
    Chart.getStyler().setPlotGridLinesVisible(true);
    Chart.getStyler().setAxisTickPadding(20);
    Chart.getStyler().setAxisTickMarkLength(15);
    Chart.getStyler().setPlotMargin(20);
    Chart.getStyler().setChartTitleVisible(false);
    Chart.getStyler().setChartTitleFont(new Font(Font.MONOSPACED,Font.BOLD, 24));
    Chart.getStyler().setLegendFont(new Font(Font.SERIF, Font.PLAIN, 18));
    // Chart.getStyler().setLegendPosition(LegendPosition.InsideSE);
    Chart.getStyler().setLegendPosition(LegendPosition.OutsideE);
    Chart.getStyler().setLegendSeriesLineLength(12);
    Chart.getStyler().setAxisTitleFont(new Font(Font.SANS_SERIF,Font.ITALIC, 18));
    Chart.getStyler().setAxisTickLabelsFont(new Font(Font.SERIF,Font.PLAIN, 11));
    //Chart.getStyler().setDayPattern("yyyy-MM");
    Chart.getStyler().setDecimalPattern("#0.00");
   
    // Config data
   
    // Training mode 
    //intWorkingMode = 0;     
    
    // Testing mode
    intWorkingMode = 1;       
    
    numberOfTrainBatchesToProcess = 1000;
    numberOfTestBatchesToProcess = 999;
    intNumberOfRowsInBatches = 1;
    intInputNeuronNumber = 1;
    intOutputNeuronNumber = 1;
    strTrainFileNameBase = "C:/My_Neural_Network_Book/Book_Examples/Work_Files/Sample8_Microbatch_Train_";
    strTestFileNameBase = "C:/My_Neural_Network_Book/Book_Examples/Work_Files/Sample8_Microbatch_Test_";
    strSaveTrainNetworkFileBase = "C:/My_Neural_Network_Book/Book_Examples/Work_Files/Sample8_Save_Network_Batch_";
    strTrainChartFileName = "C:/Book_Examples/Sample8_Chart_Train_File_Microbatch.jpg"; 
    strTestChartFileName = "C:/Book_Examples/Sample8_Chart_Test_File_Microbatch.jpg";  
    
    // Generate training batche file names and the corresponding
    // SaveNetwork file names
    
    intDayNumber = -1;  // Day number for the chart
    
    for (int i = 0; i < numberOfTrainBatchesToProcess; i++)
     {
       intDayNumber++;
      
       iString = Integer.toString(intDayNumber);
      
       if (intDayNumber >= 10 & intDayNumber < 100  )
        {
          strOutputFileName = strTrainFileNameBase + "0" + iString + ".csv"; 
          strSaveNetworkFileName = strSaveTrainNetworkFileBase + "0" +
            iString + ".csv";
        }
       else
        {   
          if (intDayNumber < 10)
           {
             strOutputFileName = strTrainFileNameBase + "00" +
               iString + ".csv"; 
             strSaveNetworkFileName = strSaveTrainNetworkFileBase + "00" +
               iString + ".csv";
           }
          else  
           {   
             strOutputFileName = strTrainFileNameBase + iString +
               ".csv"; 
             
             strSaveNetworkFileName = strSaveTrainNetworkFileBase +
               iString + ".csv";
           }
        } 
          
          strTrainingFileNames[intDayNumber] = strOutputFileName; 
          strSaveTrainNetworkFileNames[intDayNumber] = strSaveNetworkFileName;
         
     }  // End the FOR loop
      
      // Build the array linkToSaveNetworkFunctValueDiffKeys
      String tempLine;
      double tempNormFunctValueDiff = 0.00;
      double tempNormFunctValueDiffPerc = 0.00;
      double tempNormTargetFunctValueDiffPerc = 0.00;
        
      String[] tempWorkFields;
           
      try
       { 
          intDayNumber = -1;  // Day number for the chart
          
          for (int m = 0; m < numberOfTrainBatchesToProcess; m++)
            {
               intDayNumber++;
                
               BufferedReader br3 = new BufferedReader(new FileReader(strTrainingFileNames[intDayNumber])); 
               tempLine = br3.readLine();
                              
               // Skip the label record and zero batch record
               tempLine = br3.readLine();
                             
               // Brake the line using comma as separator
               tempWorkFields = tempLine.split(cvsSplitBy);
            
               tempNormFunctValueDiffPerc = Double.parseDouble(tempWorkFields[0]);
               tempNormTargetFunctValueDiffPerc = Double.parseDouble(tempWorkFields[1]);
            
               linkToSaveNetworkDayKeys[intDayNumber] = tempNormFunctValueDiffPerc;
               linkToSaveNetworkTargetFunctValueKeys[intDayNumber] = tempNormTargetFunctValueDiffPerc;
            
          }  // End the FOR loop
        
          
         // Generate testing batche file names 
         
         if(intWorkingMode == 1)
          {
            intDayNumber = -1;
         
            for (int i = 0; i < numberOfTestBatchesToProcess; i++)
             {
               intDayNumber++;
               iString = Integer.toString(intDayNumber);
            
               // Construct the testing batch names
               if (intDayNumber >= 10 & intDayNumber < 100  )
                {
                  strOutputFileName = strTestFileNameBase + "0" +
                    iString + ".csv"; 
                }              
               else
                {   
                  if (intDayNumber < 10)
                   {
                     strOutputFileName = strTestFileNameBase + "00" +
                       iString + ".csv"; 
                   }
                  else  
                   {   
                     strOutputFileName = strTestFileNameBase +
                       iString + ".csv"; 
                   }
                }
            
            strTestingFileNames[intDayNumber] = strOutputFileName; 
                   
          }  // End the FOR loop
            
         }   // End of IF
         
       }     // End for try
      catch (IOException io1)
       {
         io1.printStackTrace();
         System.exit(1);
       }
            
       // Load, train, and test Function Values file in memory
       //loadTrainFunctValueFileInMemory();
             
       // Test the mode
       if(intWorkingMode == 0)
         {
           // Train mode  
       
           int paramErrorCode;  
           int paramBatchNumber;  
           int paramR;  
           int paramDayNumber; 
           int paramS;
        
           File file1 = new File(strTrainChartFileName);
        
           if(file1.exists())
             file1.delete();    
                  
           returnCodes[0] = 0;    // Clear the error Code
           returnCodes[1] = 0;    // Set the initial batch Number to 0;  
           returnCodes[2] = 0;    // Day number; 
           
        
          do
           { 
             paramErrorCode = returnCodes[0];  
             paramBatchNumber = returnCodes[1];  
             paramDayNumber = returnCodes[2];  
            
             returnCodes = 
               trainBatches(paramErrorCode,paramBatchNumber,paramDayNumber);
           } while (returnCodes[0] > 0);
                
         }   // End the train logic        
        else
         { 
           // Testing mode
          
           File file2 = new File(strTestChartFileName);
        
           if(file2.exists())
             file2.delete(); 
            
           loadAndTestNetwork();
        
          // End the test logic
        }
        
        Encog.getInstance().shutdown();
        //System.exit(0);
        return Chart;
        
    }  // End of method
   
   
   // =======================================================
   // Load CSV to memory.
   // @return The loaded dataset.
   // =======================================================
   public static MLDataSet loadCSV2Memory(String filename, int input, int ideal, boolean headers, CSVFormat format, boolean significance)
     {
        DataSetCODEC codec = new CSVDataCODEC(new File(filename), format, headers, input, ideal, significance);
        MemoryDataLoader load = new MemoryDataLoader(codec);
        MLDataSet dataset = load.external2Memory();
        return dataset;
     }
   
   // =======================================================
   //  The main method.
   //  @param Command line arguments. No arguments are used. 
   // ======================================================
   public static void main(String[] args)
    {
      
      ExampleChart<XYChart> exampleChart = new Sample5b_Microbatches();
      XYChart Chart = exampleChart.getChart();
      new SwingWrapper<XYChart>(Chart).displayChart();  
    } // End of the main method
    
   //==========================================================================
   // This method trains batches as individual network1s 
   // saving them in separate trained datasets
   //==========================================================================
   static public int[] trainBatches(int paramErrorCode,
                                    int paramBatchNumber,int paramDayNumber)
     {
       int rBatchNumber;
       double targetToPredictFunctValueDiff = 0;
       double maxGlobalResultDiff = 0.00;
       double averGlobalResultDiff = 0.00;
       double sumGlobalResultDiff = 0.00; 
      
       double normInputFunctValueFromRecord = 0.00;
       double normTargetFunctValue1 = 0.00; 
       double normPredictFunctValue1 = 0.00;
       double denormInputDayFromRecord; 
       double denormInputFunctValueFromRecord = 0.00;
       double denormTargetFunctValue = 0.00;
       double denormPredictFunctValue1 = 0.00;
    
       BasicNetwork network1 = new BasicNetwork();  
       
       // Input layer
       network1.addLayer(new BasicLayer(null,true,intInputNeuronNumber));
      
     // Hidden layer. 
     network1.addLayer(new BasicLayer(new ActivationTANH(),true,7));
     network1.addLayer(new BasicLayer(new ActivationTANH(),true,7));
     network1.addLayer(new BasicLayer(new ActivationTANH(),true,7)); 
     network1.addLayer(new BasicLayer(new ActivationTANH(),true,7));
     network1.addLayer(new BasicLayer(new ActivationTANH(),true,7));
     //network1.addLayer(new BasicLayer(new ActivationTANH(),true,7));
     //network1.addLayer(new BasicLayer(new ActivationTANH(),true,7)); 
     //network1.addLayer(new BasicLayer(new ActivationTANH(),true,7));
     //network1.addLayer(new BasicLayer(new ActivationTANH(),true,7));
     //network1.addLayer(new BasicLayer(new ActivationTANH(),true,7)); 
       
       
      // Output layer
      network1.addLayer(new BasicLayer(new ActivationTANH(),false,intOutputNeuronNumber));
         
      network1.getStructure().finalizeStructure();
      network1.reset();
           
      maxGlobalResultDiff = 0.00;
      averGlobalResultDiff = 0.00;
      sumGlobalResultDiff = 0.00;
        
      // Loop over batches
      intDayNumber = paramDayNumber;  // Day number for the chart  
            
      for (rBatchNumber = paramBatchNumber; rBatchNumber < numberOfTrainBatchesToProcess; rBatchNumber++)
       {
         intDayNumber++;  
         
         //if(intDayNumber == 502)
         // rBatchNumber = rBatchNumber;   
         
        // Load the training file in memory
        MLDataSet trainingSet = loadCSV2Memory(strTrainingFileNames[rBatchNumber],intInputNeuronNumber,intOutputNeuronNumber,true,CSVFormat.ENGLISH,false);  
            
        // train the neural network1
        ResilientPropagation train = new ResilientPropagation(network1, trainingSet);   
      
        int epoch = 1;
        
        do
          {
    	    train.iteration();
	   
            epoch++;
         
            for (MLDataPair pair11:  trainingSet)
             {
               MLData inputData1 = pair11.getInput();
               MLData actualData1 = pair11.getIdeal();
               MLData predictData1 = network1.compute(inputData1);
        
               // These values are Normalized as the whole input is
               normInputFunctValueFromRecord = inputData1.getData(0);
               
               normTargetFunctValue1 = actualData1.getData(0);
               normPredictFunctValue1 = predictData1.getData(0); 
               
               denormInputFunctValueFromRecord =((inputDayDl - inputDayDh)*normInputFunctValueFromRecord - Nh*inputDayDl + inputDayDh*Nl)/(Nl - Nh);
               denormTargetFunctValue = ((targetFunctValueDiffPercDl - targetFunctValueDiffPercDh)*normTargetFunctValue1 - Nh*targetFunctValueDiffPercDl + targetFunctValueDiffPercDh*Nl)/(Nl - Nh);
               denormPredictFunctValue1 =((targetFunctValueDiffPercDl - targetFunctValueDiffPercDh)*normPredictFunctValue1 - Nh*targetFunctValueDiffPercDl + targetFunctValueDiffPercDh*Nl)/(Nl - Nh);
              
               //inputFunctValueFromFile = arrTrainFunctValues[rBatchNumber];
              
               targetToPredictFunctValueDiff = (Math.abs(denormTargetFunctValue - denormPredictFunctValue1)/denormTargetFunctValue)*100; 
            }
           
            //System.out.println("epoch  = " + epoch  +  "  targetToPredictFunctValueDiff = " + Math.abs(targetToPredictFunctValueDiff));
       
           if (epoch >= 1000 && Math.abs(targetToPredictFunctValueDiff) > 0.0000091) // 0.0000091  0.00091   
            { 
              returnCodes[0] = 1;
              returnCodes[1] = rBatchNumber;
              returnCodes[2] = intDayNumber-1;
              
              return returnCodes; 
             } 
           
            //System.out.println("intDayNumber = " + intDayNumber);
            
          } while(Math.abs(targetToPredictFunctValueDiff) > 0.000009);  // 0.000009  0.0009  
                        
      // This batch is optimized
      
      // Save the network1 for the currend batch
      EncogDirectoryPersistence.saveObject(new File(strSaveTrainNetworkFileNames[rBatchNumber]),network1);
           
      // Get the results after the network1 optimization
      int i = - 1; 
     
      for (MLDataPair pair1:  trainingSet)
       {
        i++;
               
        MLData inputData1 = pair1.getInput();
        MLData actualData1 = pair1.getIdeal();
        MLData predictData1 = network1.compute(inputData1);
        
        // These values are Normalized as the whole input is
        normInputFunctValueFromRecord = inputData1.getData(0);
        normTargetFunctValue1 = actualData1.getData(0);
        normPredictFunctValue1 = predictData1.getData(0); 
        
        // De-normalize the obtained values
        denormInputFunctValueFromRecord =((inputDayDl - 
          inputDayDh)*normInputFunctValueFromRecord - 
              Nh*inputDayDl + inputDayDh*Nl)/(Nl - Nh);
        
        denormTargetFunctValue = ((targetFunctValueDiffPercDl - 
          targetFunctValueDiffPercDh)*normTargetFunctValue1 - 
              Nh*targetFunctValueDiffPercDl + targetFunctValueDiffPercDh*Nl)/(Nl - Nh);
        
        denormPredictFunctValue1 =((targetFunctValueDiffPercDl - 
          targetFunctValueDiffPercDh)*normPredictFunctValue1 - 
              Nh*targetFunctValueDiffPercDl + targetFunctValueDiffPercDh*Nl)/(Nl - Nh);
              
        //inputFunctValueFromFile = arrTrainFunctValues[rBatchNumber];
                 
        targetToPredictFunctValueDiff = (Math.abs(denormTargetFunctValue - denormPredictFunctValue1)/denormTargetFunctValue)*100; 
          
        System.out.println("intDayNumber = " + intDayNumber +  "  targetFunctionValue = " + denormTargetFunctValue + "  predictFunctionValue = " + denormPredictFunctValue1 + "  valurDiff = " + targetToPredictFunctValueDiff);
        
        if (targetToPredictFunctValueDiff > maxGlobalResultDiff)
          maxGlobalResultDiff =targetToPredictFunctValueDiff;
       
        sumGlobalResultDiff = sumGlobalResultDiff +targetToPredictFunctValueDiff; 
        
        // Populate chart elements
        xData.add(denormInputFunctValueFromRecord);
        yData1.add(denormTargetFunctValue);
        yData2.add(denormPredictFunctValue1);  
        
       }  // End for FunctValue pair1 loop
     
    }  // End of the loop over batches 
    
    sumGlobalResultDiff = sumGlobalResultDiff +targetToPredictFunctValueDiff; 
    averGlobalResultDiff = sumGlobalResultDiff/numberOfTrainBatchesToProcess;
    
    // Print the max and average results
    
    System.out.println(" ");
    System.out.println(" ");
    System.out.println("maxGlobalResultDiff = " + maxGlobalResultDiff);
    System.out.println("averGlobalResultDiff = " + averGlobalResultDiff);
    
    XYSeries series1 = Chart.addSeries("Actual", xData, yData1);
    XYSeries series2 = Chart.addSeries("Forecasted", xData, yData2);
   
    series1.setMarkerColor(Color.BLACK);
    series2.setMarkerColor(Color.WHITE);
    series1.setLineStyle(SeriesLines.SOLID);
    series2.setLineStyle(SeriesLines.SOLID); 
    
    // Save the chart image
    try
      {   
        BitmapEncoder.saveBitmapWithDPI(Chart, strTrainChartFileName, BitmapFormat.JPG, 100);
      }
     catch (Exception bt)
      {
        bt.printStackTrace();
      }
        
    System.out.println ("The Chart has been saved");
    
    returnCodes[0] = 0;
    returnCodes[1] = 0;
    returnCodes[2] = 0;
          
    return returnCodes;

   }  // End of method
   
   
  //==========================================================================
  // Load the previously saved trained network1 and tests it by
  // processing the Test record
  //==========================================================================

  static public void loadAndTestNetwork()
   { 
     System.out.println("Testing the network1s results");
     
     List<Double> xData = new ArrayList<Double>();
     List<Double> yData1 = new ArrayList<Double>();   
     List<Double> yData2 = new ArrayList<Double>(); 
   
     double targetToPredictFunctValueDiff = 0;
     double maxGlobalResultDiff = 0.00;
     double averGlobalResultDiff = 0.00;
     double sumGlobalResultDiff = 0.00; 
     double maxGlobalIndex = 0;
     
     double normInputDayFromRecord1 = 0.00;
     double normTargetFunctValue1 = 0.00;
     double normPredictFunctValue1 = 0.00; 
     double denormInputDayFromRecord = 0.00;
     double denormTargetFunctValue = 0.00; 
     double denormPredictFunctValue = 0.00;     
     
     double normInputDayFromRecord2 = 0.00;
     double normTargetFunctValue2 = 0.00;
     double normPredictFunctValue2 = 0.00; 
     double denormInputDayFromRecord2 = 0.00;
     double denormTargetFunctValue2 = 0.00; 
     double denormPredictFunctValue2 = 0.00; 
     
     double normInputDayFromTestRecord = 0.00;
     double denormInputDayFromTestRecord = 0.00;
          
     double denormTargetFunctValueFromTestRecord = 0.00;
     
     String tempLine;
     String[] tempWorkFields;
     double dayKeyFromTestRecord = 0.00;
     double targetFunctValueFromTestRecord = 0.00;
     double r1 = 0.00;
     double r2 = 0.00;
     BufferedReader br4; 
         
     BasicNetwork network1;
     BasicNetwork network2;
     int k1 = 0;
     int k3 = 0;
     
    try
     {   
        // Process testing records
        maxGlobalResultDiff = 0.00;
        averGlobalResultDiff = 0.00;
        sumGlobalResultDiff = 0.00;  
                 
        for (k1 = 0; k1 < numberOfTestBatchesToProcess; k1++)
         {
            if(k1 == 100)
              k1 = k1;  
            
            // Read the corresponding test micro-batch file.
            br4 = new BufferedReader(new FileReader(strTestingFileNames[k1])); 
            tempLine = br4.readLine();
              
            // Skip the label record 
            tempLine = br4.readLine();
                                      
            // Brake the line using comma as separator
            tempWorkFields = tempLine.split(cvsSplitBy);
            
            dayKeyFromTestRecord = Double.parseDouble(tempWorkFields[0]);
            targetFunctValueFromTestRecord = Double.parseDouble(tempWorkFields[1]);
            
            // Denormalize the dayKeyFromTestRecord 
            denormInputDayFromTestRecord = 
              ((inputDayDl - inputDayDh)*dayKeyFromTestRecord -
                      Nh*inputDayDl + inputDayDh*Nl)/(Nl - Nh);
              
              // Denormalize the targetFunctValueFromTestRecord 
            denormTargetFunctValueFromTestRecord = ((targetFunctValueDiffPercDl -
               targetFunctValueDiffPercDh)*targetFunctValueFromTestRecord -
                 Nh*targetFunctValueDiffPercDl + targetFunctValueDiffPercDh*Nl)/(Nl - Nh);
            
            // Load the corresponding training micro-batch dataset in memory
            MLDataSet trainingSet1 = loadCSV2Memory(strTrainingFileNames[k1],intInputNeuronNumber,intOutputNeuronNumber,true,CSVFormat.ENGLISH,false);             
            
            //MLDataSet testingSet =
            //   loadCSV2Memory(strTestingFileNames[k1],intInputNeuronNumber,
            //     intOutputNeuronNumber,true,CSVFormat.ENGLISH,false);
                         
            network1 =
             (BasicNetwork)EncogDirectoryPersistence.
               loadObject(new File(strSaveTrainNetworkFileNames[k1]));
                                
             // Get the results after the network1 optimization
             int iMax = 0;
             int i = - 1; // Index of the array to get results
      
             for (MLDataPair pair1:  trainingSet1)
              {
                i++;
                iMax = i+1;
        
                MLData inputData1 = pair1.getInput();
                MLData actualData1 = pair1.getIdeal();
                MLData predictData1 = network1.compute(inputData1);
 
                // These values are Normalized as the whole input is
                normInputDayFromRecord1 = inputData1.getData(0);
                normTargetFunctValue1 = actualData1.getData(0);
                normPredictFunctValue1 = predictData1.getData(0); 
               
                denormInputDayFromRecord = 
                   ((inputDayDl - inputDayDh)*normInputDayFromRecord1 -
                      Nh*inputDayDl + inputDayDh*Nl)/(Nl - Nh);
                 
                denormTargetFunctValue = ((targetFunctValueDiffPercDl -
                   targetFunctValueDiffPercDh)*normTargetFunctValue1 -
                     Nh*targetFunctValueDiffPercDl +
                      targetFunctValueDiffPercDh*Nl)/(Nl - Nh);
                 
                denormPredictFunctValue =((targetFunctValueDiffPercDl - 
                   targetFunctValueDiffPercDh)*normPredictFunctValue1 -
                     Nh*targetFunctValueDiffPercDl +
                        targetFunctValueDiffPercDh*Nl)/(Nl - Nh);
             
                targetToPredictFunctValueDiff = (Math.abs(denormTargetFunctValue - denormPredictFunctValue)/denormTargetFunctValue)*100; 
 
                System.out.println("Record Number = " + k1 + "  DayNumber = " + denormInputDayFromTestRecord +
                 "  denormTargetFunctValueFromTestRecord = " + denormTargetFunctValueFromTestRecord +
                 "  denormPredictFunctValue = " + denormPredictFunctValue +
                 "  valurDiff = " + targetToPredictFunctValueDiff);
      
                 
                if (targetToPredictFunctValueDiff > maxGlobalResultDiff)
                 { 
                   maxGlobalIndex = iMax;  
                   maxGlobalResultDiff =targetToPredictFunctValueDiff;
                 }
        
                sumGlobalResultDiff = sumGlobalResultDiff +
                  targetToPredictFunctValueDiff; 
                        
                // Populate chart elements
       
                xData.add(denormInputDayFromTestRecord);
                yData1.add(denormTargetFunctValueFromTestRecord);
                yData2.add(denormPredictFunctValue);
         
           }  // End for pair2 loop    
               
         }   // End of loop using k1
    
        // Print the max and average results
        
        System.out.println(" ");  
        
        averGlobalResultDiff = sumGlobalResultDiff/numberOfTestBatchesToProcess;
                  
        System.out.println("maxGlobalResultDiff = " + maxGlobalResultDiff +
          "  i = " + maxGlobalIndex);
        System.out.println("averGlobalResultDiff = " + averGlobalResultDiff);
        
      }     // End of TRY
        catch (FileNotFoundException nf)
      {
            nf.printStackTrace();
      } 
     catch (IOException e1)
      {
            e1.printStackTrace();
      } 
     
    // All testing batch files have been processed
    XYSeries series1 = Chart.addSeries("Actual", xData, yData1);
    XYSeries series2 = Chart.addSeries("Forecasted", xData, yData2);
    
    series1.setLineColor(XChartSeriesColors.BLACK);
    series2.setLineColor(XChartSeriesColors.LIGHT_GREY);
      
    series1.setMarkerColor(Color.BLACK);
    series2.setMarkerColor(Color.WHITE);
    series1.setLineStyle(SeriesLines.SOLID);
    series2.setLineStyle(SeriesLines.SOLID); 
    
    // Save the chart image
    try
     {   
         BitmapEncoder.saveBitmapWithDPI(Chart, strTrainChartFileName, 
           BitmapFormat.JPG, 100);
     }
    catch (Exception bt)
     {
       bt.printStackTrace();
     }
        
    System.out.println ("The Chart has been saved");
    System.out.println("End of testing for mini-batches training");      
  
   } // End of the method
     
 } // End of the  Encog class
