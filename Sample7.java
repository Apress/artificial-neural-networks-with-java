// =============================================================================
// Approximate the SPY prices function using the micro-batch method.
// Each micro-batch dataset includes the label record and the data record.
// The data record contains 12 inputPriceDiffPerc fields plus one
// targetPriceDiffPerc field.
//  
// The number of inputLayer neurons is 12
// The number of outputLayer neurons is 1
// =============================================================================

package saple7;

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
import java.io.BufferedWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
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

public class Saple7 implements ExampleChart<XYChart>
{
   // Normalization parameters 
    
   // Normalizing interval  
   static double Nh =  1;  
   static double Nl = -1;
   
   // inputPriceDiffPerc
   static double inputPriceDiffPercDh = 10.00; 
   static double inputPriceDiffPercDl = -20.00;
   
   // targetPriceDiffPerc
   static double targetPriceDiffPercDh = 10.00; 
   static double targetPriceDiffPercDl = -20.00;
   
   static String cvsSplitBy = ",";
   static Properties prop = null;
   static Date workDate = null;
   static int paramErrorCode = 0;  
   static int paramBatchNumber = 0;  
   static int paramDayNumber = 0;  
   static String strWorkingMode;
   static String strNumberOfBatchesToProcess;
   static String strNumberOfRowsInInputFile; 
   static String strNumberOfRowsInBatches; 
   static String strIputNeuronNumber;
   static String strOutputNeuronNumber;
   static String strNumberOfRecordsInTestFile; 
   static String strInputFileNameBase;
   static String strTestFileNameBase;
   static String strSaveNetworkFileNameBase; 
   static String strTrainFileName;
   static String strValidateFileName;
   static String strChartFileName;
   static String strDatesTrainFileName; 
   static String strPricesFileName; 
   static int intWorkingMode;
   static int intNumberOfBatchesToProcess;
   static int intNumberOfRowsInBatches; 
   static int intInputNeuronNumber;
   static int intOutputNeuronNumber;
   static String strOutputFileName;
   static String strSaveNetworkFileName;
   static String strNumberOfMonths;
   static String strYearMonth;
   static XYChart Chart;
   static String iString;
   static double inputPriceFromFile;
     
   //static List<Date> xData = new ArrayList<Date>();
   static List<Double> xData = new ArrayList<Double>();
   static List<Double> yData1 = new ArrayList<Double>();   
   static List<Double> yData2 = new ArrayList<Double>();   
   
   // These arrays is where the two Date files are loaded 
   static Date[] yearDateTraining = new Date[150];
   static String[] strTrainingFileNames = new String[150]; 
   static String[] strTestingFileNames = new String[150]; 
   static String[] strSaveNetworkFileNames = new String[150]; 
   
   static BufferedReader br3;
   
   static double recordNormInputPriceDiffPerc_00 = 0.00;
   static double recordNormInputPriceDiffPerc_01 = 0.00;
   static double recordNormInputPriceDiffPerc_02 = 0.00;
   static double recordNormInputPriceDiffPerc_03 = 0.00;
   static double recordNormInputPriceDiffPerc_04 = 0.00;
   static double recordNormInputPriceDiffPerc_05 = 0.00;
   static double recordNormInputPriceDiffPerc_06 = 0.00;
   static double recordNormInputPriceDiffPerc_07 = 0.00;
   static double recordNormInputPriceDiffPerc_08 = 0.00;
   static double recordNormInputPriceDiffPerc_09 = 0.00;
   static double recordNormInputPriceDiffPerc_10 = 0.00;
   static double recordNormInputPriceDiffPerc_11 = 0.00;
   
   static double recordNormTargetPriceDiffPerc = 0.00;
   static double tempMonth = 0.00;
   static int intNumberOfSavedNetworks = 0;
   
   static double[] linkToSaveInputPriceDiffPerc_00 = new double[150];
   static double[] linkToSaveInputPriceDiffPerc_01 = new double[150];
   static double[] linkToSaveInputPriceDiffPerc_02 = new double[150];
   static double[] linkToSaveInputPriceDiffPerc_03 = new double[150];
   static double[] linkToSaveInputPriceDiffPerc_04 = new double[150];
   static double[] linkToSaveInputPriceDiffPerc_05 = new double[150];
   static double[] linkToSaveInputPriceDiffPerc_06 = new double[150];
   static double[] linkToSaveInputPriceDiffPerc_07 = new double[150];
   static double[] linkToSaveInputPriceDiffPerc_08 = new double[150];
   static double[] linkToSaveInputPriceDiffPerc_09 = new double[150];
   static double[] linkToSaveInputPriceDiffPerc_10 = new double[150];
   static double[] linkToSaveInputPriceDiffPerc_11 = new double[150];
   
   static int[] returnCodes  = new int[3];
   static int intDayNumber = 0;
   static File file2 = null;
   static double[] linkToSaveTargetPriceDiffPerc = new double[150];
   static double[] arrPrices = new double[150]; 
   
  @Override
  public XYChart getChart()
   {
    
    // Create Chart
    
    Chart = new XYChartBuilder().width(900).height(500).title(getClass().getSimpleName()).xAxisTitle("Month").yAxisTitle("Price").build();
    
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
    Chart.getStyler().setChartTitleFont(new Font(Font.MONOSPACED, Font.BOLD, 24));
    Chart.getStyler().setLegendFont(new Font(Font.SERIF, Font.PLAIN, 18));
    // Chart.getStyler().setLegendPosition(LegendPosition.InsideSE);
    Chart.getStyler().setLegendPosition(LegendPosition.OutsideE);
    Chart.getStyler().setLegendSeriesLineLength(12);
    Chart.getStyler().setAxisTitleFont(new Font(Font.SANS_SERIF, Font.ITALIC, 18));
    Chart.getStyler().setAxisTickLabelsFont(new Font(Font.SERIF, Font.PLAIN, 11));
    Chart.getStyler().setDatePattern("yyyy-MM");
    Chart.getStyler().setDecimalPattern("#0.00");
       
    // Training configuration
    intWorkingMode = 0;
    intNumberOfBatchesToProcess = 120;
    strInputFileNameBase = "C:/My_Neural_Network_Book/Temp_Files/Sample7_Microbatches_Train_Batch_";
    strSaveNetworkFileNameBase = "C:/My_Neural_Network_Book/Temp_Files/Sample7_Save_Network_Batch_";
    strChartFileName = "C:/My_Neural_Network_Book/Temp_Files/Sample7_XYLineChart_Train.jpg";      
    strDatesTrainFileName = "C:/My_Neural_Network_Book/Book_Examples/Sample7_Dates_Real_SP500_3000.csv"; 
    strPricesFileName = "C:/My_Neural_Network_Book/Book_Examples/Sample7_InputPrice_SP500_200001_200901.csv";
    
    // Testing configuration
    //intWorkingMode = 1;
    //intNumberOfBatchesToProcess = 121;
    //intNumberOfSavedNetworks = 120;
    //strInputFileNameBase = "C:/My_Neural_Network_Book/Temp_Files/Sample7_Microbatches_Test_Batch_";
    //strSaveNetworkFileNameBase = "C:/My_Neural_Network_Book/Temp_Files/Sample7_Save_Network_Batch_";
    //strChartFileName = "C:/My_Neural_Network_Book/Book_Examples/Sample7_XYLineChart_Test.jpg";      
    //strDatesTrainFileName = "C:/My_Neural_Network_Book/Book_Examples/Sample7_Dates_Real_SP500_3000.csv"; 
    //strPricesFileName = "C:/My_Neural_Network_Book/Book_Examples/Sample7_InputPrice_SP500_200001_200901.csv";
   
    // Commom configuration
    intNumberOfRowsInBatches = 1;
    intInputNeuronNumber = 12;
    intOutputNeuronNumber = 1;
     
    // Generate training batche file names and the corresponding SaveNetwork file names
    for (int i = 0; i < intNumberOfBatchesToProcess; i++)
     {
       iString = Integer.toString(i);
            
       // Construct the training batch names
       if (i < 10)
        {
          strOutputFileName = strInputFileNameBase + "00" + iString + ".csv"; 
          strSaveNetworkFileName = strSaveNetworkFileNameBase + "00" + iString + ".csv";
        }
       else
        {  
          if(i >=10 && i < 100)
           {  
            strOutputFileName = strInputFileNameBase + "0" + iString + ".csv";
            strSaveNetworkFileName = strSaveNetworkFileNameBase + "0" + iString + ".csv";
           }  
          else
           {   
             strOutputFileName = strInputFileNameBase + iString + ".csv";
             strSaveNetworkFileName = strSaveNetworkFileNameBase + iString + ".csv";
           }
          
        }  
      
      strSaveNetworkFileNames[i] = strSaveNetworkFileName;
       
      if(intWorkingMode == 0)
       {   
        strTrainingFileNames[i] = strOutputFileName;
      
        File file1 = new File(strSaveNetworkFileNames[i]);
       
        if(file1.exists())
            file1.delete();    
       }
      else
       strTestingFileNames[i] = strOutputFileName;
          
     }  // End the FOR loop
      
      // Build the array linkToSaveInputPriceDiffPerc_01
      String tempLine = null; 
      String[] tempWorkFields = null;
      
      recordNormInputPriceDiffPerc_00 = 0.00;
      recordNormInputPriceDiffPerc_01 = 0.00;
      recordNormInputPriceDiffPerc_02 = 0.00;
      recordNormInputPriceDiffPerc_03 = 0.00;
      recordNormInputPriceDiffPerc_04 = 0.00;
      recordNormInputPriceDiffPerc_05 = 0.00;
      recordNormInputPriceDiffPerc_06 = 0.00;
      recordNormInputPriceDiffPerc_07 = 0.00;
      recordNormInputPriceDiffPerc_08 = 0.00;
      recordNormInputPriceDiffPerc_09 = 0.00;
      recordNormInputPriceDiffPerc_10 = 0.00;
      recordNormInputPriceDiffPerc_11 = 0.00;
      
      double recordNormTargetPriceDiffPerc = 0.00;
      
      try
       { 
          for (int m = 0; m < intNumberOfBatchesToProcess; m++)
            {
               if(intWorkingMode == 0)
                 br3 = new BufferedReader(new FileReader(strTrainingFileNames[m]));
               
               if(intWorkingMode == 1)
                 br3 = new BufferedReader(new FileReader(strTestingFileNames[m]));  
            
               // Skip the label record 
               tempLine = br3.readLine();
               tempLine = br3.readLine();
               
               // Brake the line using comma as separator
               tempWorkFields = tempLine.split(cvsSplitBy);
            
               recordNormInputPriceDiffPerc_00 = Double.parseDouble(tempWorkFields[0]);
               recordNormInputPriceDiffPerc_01 = Double.parseDouble(tempWorkFields[1]);
               recordNormInputPriceDiffPerc_02 = Double.parseDouble(tempWorkFields[2]);
               recordNormInputPriceDiffPerc_03 = Double.parseDouble(tempWorkFields[3]);
               recordNormInputPriceDiffPerc_04 = Double.parseDouble(tempWorkFields[4]);
               recordNormInputPriceDiffPerc_05 = Double.parseDouble(tempWorkFields[5]);
               recordNormInputPriceDiffPerc_06 = Double.parseDouble(tempWorkFields[6]);
               recordNormInputPriceDiffPerc_07 = Double.parseDouble(tempWorkFields[7]);
               recordNormInputPriceDiffPerc_08 = Double.parseDouble(tempWorkFields[8]);
               recordNormInputPriceDiffPerc_09 = Double.parseDouble(tempWorkFields[9]);
               recordNormInputPriceDiffPerc_10 = Double.parseDouble(tempWorkFields[10]);
               recordNormInputPriceDiffPerc_11 = Double.parseDouble(tempWorkFields[11]);
               
               recordNormTargetPriceDiffPerc = Double.parseDouble(tempWorkFields[12]);
               
               linkToSaveInputPriceDiffPerc_00[m] = recordNormInputPriceDiffPerc_00;
               linkToSaveInputPriceDiffPerc_01[m] = recordNormInputPriceDiffPerc_01;
               linkToSaveInputPriceDiffPerc_02[m] = recordNormInputPriceDiffPerc_02;
               linkToSaveInputPriceDiffPerc_03[m] = recordNormInputPriceDiffPerc_03;
               linkToSaveInputPriceDiffPerc_04[m] = recordNormInputPriceDiffPerc_04;
               linkToSaveInputPriceDiffPerc_05[m] = recordNormInputPriceDiffPerc_05;
               linkToSaveInputPriceDiffPerc_06[m] = recordNormInputPriceDiffPerc_06;
               linkToSaveInputPriceDiffPerc_07[m] = recordNormInputPriceDiffPerc_07;
               linkToSaveInputPriceDiffPerc_08[m] = recordNormInputPriceDiffPerc_08;
               linkToSaveInputPriceDiffPerc_09[m] = recordNormInputPriceDiffPerc_09;
               linkToSaveInputPriceDiffPerc_10[m] = recordNormInputPriceDiffPerc_10;
               linkToSaveInputPriceDiffPerc_11[m] = recordNormInputPriceDiffPerc_11;
                              
               linkToSaveTargetPriceDiffPerc[m] = recordNormTargetPriceDiffPerc;
                           
          }  // End the FOR loop
        
                   
       // Load Dates into memory
       loadDatesInMemory();
       
       // Load Prices into memory
       loadPriceFileInMemory();
      
       file2 = new File(strChartFileName);
       
       if(file2.exists())
         file2.delete();    
      
       // Test the working mode
       if(intWorkingMode == 0)
         {
           // Train batches and save the trained networks  
           int  paramBatchNumber;
          
           returnCodes[0] = 0;    // Clear the error Code
           returnCodes[1] = 0;    // Set the initial batch Number to 1; 
           returnCodes[2] = 0;    // Set the initial day number; 
           
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
          if(intWorkingMode == 1) 
           {  
              // Load and test the network logic
              loadAndTestNetwork();
           }
          
        }  // End of ELSE
       
     }     // End of Try
    catch (Exception e1)
     {
       e1.printStackTrace();
     }
      
    Encog.getInstance().shutdown();
     
    return Chart;
    
   }  // End of method
   
   // =======================================================
   // Load CSV to memory.
   // @return The loaded dataset.
   // =======================================================
   public static MLDataSet loadCSV2Memory(String filename, int input, int ideal, 
     boolean headers, CSVFormat format, boolean significance)
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
      ExampleChart<XYChart> exampleChart = new Saple7();
      XYChart Chart = exampleChart.getChart();
      new SwingWrapper<XYChart>(Chart).displayChart();  
    } // End of the main method
   
    
   //==========================================================================
   // Mode 0
   // Train batches as individual networks, saving them in separate files
   //==========================================================================
   static public int[] trainBatches(int paramErrorCode,int paramBatchNumber,
         int paramDayNumber)
     {
       int rBatchNumber;
       
       double realDenormTargetToPredictPricePerc = 0;
       double maxGlobalResultDiff = 0.00;
       double averGlobalResultDiff = 0.00;
       double sumGlobalResultDiff = 0.00; 
      double normTargetPriceDiffPerc = 0.00; 
       double normPredictPriceDiffPerc = 0.00;
       double normInputPriceDiffPercFromRecord = 0.00;
       double denormTargetPriceDiffPerc;
       double denormPredictPriceDiffPerc;
       double denormInputPriceDiffPercFromRecord;
       double workNormInputPrice;
       Date tempDate;
       double trainError;
       double realDenormPredictPrice;
       double realDenormTargetPrice;
       
       // Build the network
       BasicNetwork network = new BasicNetwork();  
              
       // Input layer
      network.addLayer(new BasicLayer(null,true,intInputNeuronNumber));
      
      // Hidden layer. 
      network.addLayer(new BasicLayer(new ActivationTANH(),true,25));
      network.addLayer(new BasicLayer(new ActivationTANH(),true,25));
      network.addLayer(new BasicLayer(new ActivationTANH(),true,25)); 
      network.addLayer(new BasicLayer(new ActivationTANH(),true,25));
      network.addLayer(new BasicLayer(new ActivationTANH(),true,25));
      network.addLayer(new BasicLayer(new ActivationTANH(),true,25));
      network.addLayer(new BasicLayer(new ActivationTANH(),true,25)); 
            
      // Output layer
      network.addLayer(new BasicLayer(new ActivationTANH(),false,intOutputNeuronNumber));
         
      network.getStructure().finalizeStructure();
      network.reset();
              
      // Loop over batches
     intDayNumber = paramDayNumber;  // Day number for the chart  
      
     for (rBatchNumber = paramBatchNumber; rBatchNumber < intNumberOfBatchesToProcess; rBatchNumber++)
      {
        intDayNumber++;  
 
        //if(rBatchNumber == 201)
        // rBatchNumber = rBatchNumber;
            
        // Load the training CVS file for the current batch in memory
        MLDataSet trainingSet = loadCSV2Memory(strTrainingFileNames[rBatchNumber],
          intInputNeuronNumber,intOutputNeuronNumber,true,CSVFormat.ENGLISH,false);  
            
        // train the neural network
        ResilientPropagation train = new ResilientPropagation(network, trainingSet);   
      
        int epoch = 1;
        double tempLastErrorPerc = 0.00;      
                
        do
          {
            train.iteration();
	               
            epoch++;
         
            for (MLDataPair pair1:  trainingSet)
             {
               MLData inputData = pair1.getInput();
               MLData actualData = pair1.getIdeal();
               MLData predictData = network.compute(inputData);
        
               // These values are normalized 
               normTargetPriceDiffPerc = actualData.getData(0);
               normPredictPriceDiffPerc = predictData.getData(0); 
               
               // Denormalize these values
               denormTargetPriceDiffPerc = ((targetPriceDiffPercDl - targetPriceDiffPercDh)*
                 normTargetPriceDiffPerc - Nh*targetPriceDiffPercDl + targetPriceDiffPercDh*Nl)/(Nl - Nh);
               denormPredictPriceDiffPerc =((targetPriceDiffPercDl - targetPriceDiffPercDh)*
                 normPredictPriceDiffPerc - Nh*targetPriceDiffPercDl + targetPriceDiffPercDh*Nl)/(Nl - Nh);
               
               inputPriceFromFile = arrPrices[rBatchNumber+12];
              
               realDenormTargetPrice = inputPriceFromFile + inputPriceFromFile*
                 denormTargetPriceDiffPerc/100;
               
               realDenormPredictPrice = inputPriceFromFile + inputPriceFromFile*
                 denormPredictPriceDiffPerc/100;
               
               realDenormTargetToPredictPricePerc = (Math.abs(realDenormTargetPrice - realDenormPredictPrice)/
                 realDenormTargetPrice)*100; 
       
             }
             
           if (epoch >= 500 && realDenormTargetToPredictPricePerc > 0.00091)    
             { 
              returnCodes[0] = 1;
              returnCodes[1] = rBatchNumber;
              returnCodes[2] = intDayNumber-1;
                            
              //System.out.println("Try again");  
              return returnCodes; 
             }      
          
             //System.out.println(realDenormTargetToPredictPricePerc);  
          } while(realDenormTargetToPredictPricePerc >  0.0009);    
          
            
      // This batch is optimized
      
      // Save the network for the currend batch
      EncogDirectoryPersistence.saveObject(new File(strSaveNetworkFileNames[rBatchNumber]),network);
     
      // Print the trained neural network resuls for the batch
      //System.out.println("Trained Neural Network Results");
      
      // Get the results after the network optimization
      int i = - 1; // Index of the array to get results
    
     maxGlobalResultDiff = 0.00;
     averGlobalResultDiff = 0.00;
     sumGlobalResultDiff = 0.00;
     
    //if (rBatchNumber == 857)
    //    i = i; 
     
    // Validation
    for (MLDataPair pair:  trainingSet)
      {
        i++;
               
        MLData inputData = pair.getInput();
        MLData actualData = pair.getIdeal();
        MLData predictData = network.compute(inputData);
        
        // These values are Normalized as the whole input is
        normTargetPriceDiffPerc = actualData.getData(0);
        normPredictPriceDiffPerc = predictData.getData(0);
        //normInputPriceDiffPercFromRecord[i] = inputData.getData(0);
        normInputPriceDiffPercFromRecord = inputData.getData(0);
     
        // Denormalise this data to show the real result value
        denormTargetPriceDiffPerc = ((targetPriceDiffPercDl - targetPriceDiffPercDh)*
          normTargetPriceDiffPerc - Nh*targetPriceDiffPercDl + targetPriceDiffPercDh*Nl)/(Nl - Nh);
        
        denormPredictPriceDiffPerc =((targetPriceDiffPercDl - targetPriceDiffPercDh)*
          normPredictPriceDiffPerc - Nh*targetPriceDiffPercDl + targetPriceDiffPercDh*Nl)/(Nl - Nh);
        
        denormInputPriceDiffPercFromRecord = ((inputPriceDiffPercDl - inputPriceDiffPercDh)*
          normInputPriceDiffPercFromRecord - Nh*inputPriceDiffPercDl + inputPriceDiffPercDh*Nl)/(Nl - Nh);
        
        // Get the price of the 12th element of the row
        inputPriceFromFile = arrPrices[rBatchNumber+12];
                
        // Convert denormPredictPriceDiffPerc and denormTargetPriceDiffPerc to a real denorm pricse 
        
        realDenormTargetPrice = inputPriceFromFile + inputPriceFromFile*(denormTargetPriceDiffPerc/100);
        realDenormPredictPrice = inputPriceFromFile + inputPriceFromFile*(denormPredictPriceDiffPerc/100);
        
        //System.out.println("Record = " + rBatchNumber + "  inputPriceFromFile = " + inputPriceFromFile + "   denormTargetPriceDiffPerc = " + denormTargetPriceDiffPerc + "  realDenormTargetPrice = " + realDenormTargetPrice);
        //System.out.println("Record = " + rBatchNumber + "  inputPriceFromFile = " + inputPriceFromFile + "   denormPredictPriceDiffPerc = " + denormPredictPriceDiffPerc + "  realDenormPredictPrice = " + realDenormPredictPrice);
        
        
        realDenormTargetToPredictPricePerc = (Math.abs(realDenormTargetPrice - realDenormPredictPrice)/
          realDenormTargetPrice)*100; 
          
        System.out.println("Month = " + (rBatchNumber+1) + "  targetPrice = " + realDenormTargetPrice + 
          "  predictPrice = " + realDenormPredictPrice + "  diff = " + realDenormTargetToPredictPricePerc);
         
         if (realDenormTargetToPredictPricePerc > maxGlobalResultDiff)
           { 
             maxGlobalResultDiff = realDenormTargetToPredictPricePerc;
           }
        
        sumGlobalResultDiff = sumGlobalResultDiff + realDenormTargetToPredictPricePerc; 
        
        // Populate chart elements
        tempDate = yearDateTraining[rBatchNumber+14];
        //xData.add(tempDate);
        tempMonth = (double) rBatchNumber+14;
        xData.add(tempMonth);
        yData1.add(realDenormTargetPrice);
        yData2.add(realDenormPredictPrice);
        
       }  // End for Price pair loop
        
     }  // End of the loop over batches 
    
     XYSeries series1 = Chart.addSeries("Actual price", xData, yData1);
     XYSeries series2 = Chart.addSeries("Predicted price", xData, yData2);
    
     series1.setLineColor(XChartSeriesColors.BLUE);
     series2.setMarkerColor(Color.ORANGE);
     series1.setLineStyle(SeriesLines.SOLID);
     series2.setLineStyle(SeriesLines.SOLID);
     
      // Print the max and average results
           
      averGlobalResultDiff = sumGlobalResultDiff/intNumberOfBatchesToProcess;
       
      System.out.println(" ");
      System.out.println("maxGlobalResultDiff = " + maxGlobalResultDiff);
      System.out.println("averGlobalResultDiff = " + averGlobalResultDiff);
      System.out.println(" ");
         
      // Save the chart image
      try
       {   
         BitmapEncoder.saveBitmapWithDPI(Chart, strChartFileName, BitmapFormat.JPG, 100);
       }
      catch (Exception bt)
       {
         bt.printStackTrace();
       }
       
      System.out.println ("Chart and Network have been saved");
      System.out.println("End of validating batches for training");
   
      returnCodes[0] = 0;
      returnCodes[1] = 0;
      returnCodes[2] = 0;
      
      return returnCodes;
   }  // End of method
   
  //==========================================================================
  // Mode 1
  // Load the previously saved trained network and process test mini-batches
  //==========================================================================

  static public void loadAndTestNetwork()
   { 
     System.out.println("Testing the networks results");
     
     List<Double> xData = new ArrayList<Double>();
     List<Double> yData1 = new ArrayList<Double>();   
     List<Double> yData2 = new ArrayList<Double>(); 
    
     double realDenormTargetToPredictPricePerc = 0;
     double maxGlobalResultDiff = 0.00;
     double averGlobalResultDiff = 0.00;
     double sumGlobalResultDiff = 0.00; 
     double maxGlobalIndex = 0;
     
     recordNormInputPriceDiffPerc_00 = 0.00;
     recordNormInputPriceDiffPerc_01 = 0.00;
     recordNormInputPriceDiffPerc_02 = 0.00;
     recordNormInputPriceDiffPerc_03 = 0.00;
     recordNormInputPriceDiffPerc_04 = 0.00;
     recordNormInputPriceDiffPerc_05 = 0.00;
     recordNormInputPriceDiffPerc_06 = 0.00;
     recordNormInputPriceDiffPerc_07 = 0.00;
     recordNormInputPriceDiffPerc_08 = 0.00;
     recordNormInputPriceDiffPerc_09 = 0.00;
     recordNormInputPriceDiffPerc_10 = 0.00;
     recordNormInputPriceDiffPerc_11 = 0.00;
           
     double recordNormTargetPriceDiffPerc = 0.00;
     double normTargetPriceDiffPerc; 
     double normPredictPriceDiffPerc;
     double normInputPriceDiffPercFromRecord;
     double denormTargetPriceDiffPerc;
     double denormPredictPriceDiffPerc;
     double denormInputPriceDiffPercFromRecord;
     double realDenormTargetPrice = 0.00;
     double realDenormPredictPrice = 0.00;
     double minVectorValue = 0.00;
     String tempLine;
     String[] tempWorkFields;
     int tempMinIndex = 0;
     double rTempPriceDiffPerc = 0.00;
     double rTempKey = 0.00;
     double vectorForNetworkRecord = 0.00;
     double r_00 = 0.00;
     double r_01 = 0.00;
     double r_02 = 0.00;
     double r_03 = 0.00;
     double r_04 = 0.00;
     double r_05 = 0.00;
     double r_06 = 0.00;
     double r_07 = 0.00;
     double r_08 = 0.00;
     double r_09 = 0.00;
     double r_10 = 0.00;
     double r_11 = 0.00;
     double vectorDiff;
     double r1 = 0.00;
     double r2 = 0.00;
     double vectorForRecord = 0.00;
     int k1 = 0;
     int k3 = 0;
     
     BufferedReader br4; 
     BasicNetwork network;
    
     try
      {   
        maxGlobalResultDiff = 0.00;
        averGlobalResultDiff = 0.00;
        sumGlobalResultDiff = 0.00;  
       
        for (k1 = 0; k1 < intNumberOfBatchesToProcess; k1++)
         {
           //if(k1 == 120)
           //   k1 = k1;  
           
            br4 = new BufferedReader(new FileReader(strTestingFileNames[k1])); 
            tempLine = br4.readLine();
              
            // Skip the label record 
            tempLine = br4.readLine();
                                      
            // Brake the line using comma as separator
            tempWorkFields = tempLine.split(cvsSplitBy);
            
            recordNormInputPriceDiffPerc_00 = Double.parseDouble(tempWorkFields[0]);
            recordNormInputPriceDiffPerc_01 = Double.parseDouble(tempWorkFields[1]);
            recordNormInputPriceDiffPerc_02 = Double.parseDouble(tempWorkFields[2]);
            recordNormInputPriceDiffPerc_03 = Double.parseDouble(tempWorkFields[3]);
            recordNormInputPriceDiffPerc_04 = Double.parseDouble(tempWorkFields[4]);
            recordNormInputPriceDiffPerc_05 = Double.parseDouble(tempWorkFields[5]);
            recordNormInputPriceDiffPerc_06 = Double.parseDouble(tempWorkFields[6]);
            recordNormInputPriceDiffPerc_07 = Double.parseDouble(tempWorkFields[7]);
            recordNormInputPriceDiffPerc_08 = Double.parseDouble(tempWorkFields[8]);
            recordNormInputPriceDiffPerc_09 = Double.parseDouble(tempWorkFields[9]);
            recordNormInputPriceDiffPerc_10 = Double.parseDouble(tempWorkFields[10]);
            recordNormInputPriceDiffPerc_11 = Double.parseDouble(tempWorkFields[11]);
                                  
            recordNormTargetPriceDiffPerc = Double.parseDouble(tempWorkFields[12]);
            
            if(k1 < 120)
             {
               // Load the network for the current record
               network =
                 (BasicNetwork)EncogDirectoryPersistence.loadObject(new File(strSaveNetworkFileNames[k1])); 
                   
             // Load the training file record
             MLDataSet testingSet =
               loadCSV2Memory(strTestingFileNames[k1],intInputNeuronNumber,intOutputNeuronNumber,true,
                 CSVFormat.ENGLISH,false);
        
            // Get the results from the loaded previously saved networks
             int i = - 1; 
      
             for (MLDataPair pair:  testingSet)
              {
                 i++;
                              
                 MLData inputData = pair.getInput();
                 MLData actualData = pair.getIdeal();
                 MLData predictData = network.compute(inputData);
        
                 // These values are Normalized as the whole input is
                 normTargetPriceDiffPerc = actualData.getData(0);
                 normPredictPriceDiffPerc = predictData.getData(0);
                 normInputPriceDiffPercFromRecord = inputData.getData(11);
          
                 // Denormalise this data 
                 denormTargetPriceDiffPerc = ((targetPriceDiffPercDl - targetPriceDiffPercDh)*normTargetPriceDiffPerc - Nh*targetPriceDiffPercDl + targetPriceDiffPercDh*Nl)/(Nl - Nh);
                 denormPredictPriceDiffPerc =((targetPriceDiffPercDl - targetPriceDiffPercDh)* normPredictPriceDiffPerc - Nh*targetPriceDiffPercDl + targetPriceDiffPercDh*Nl)/(Nl - Nh);
                 denormInputPriceDiffPercFromRecord = ((inputPriceDiffPercDl - inputPriceDiffPercDh)*normInputPriceDiffPercFromRecord - Nh*inputPriceDiffPercDl + inputPriceDiffPercDh*Nl)/(Nl - Nh);
                         
                 inputPriceFromFile = arrPrices[k1+12];
                 
                 // Convert denormPredictPriceDiffPerc and denormTargetPriceDiffPerc to a real denorm pricse 
                 realDenormTargetPrice = inputPriceFromFile + inputPriceFromFile*(denormTargetPriceDiffPerc/100);
                 realDenormPredictPrice = inputPriceFromFile + inputPriceFromFile*(denormPredictPriceDiffPerc/100);
                                        
                 realDenormTargetToPredictPricePerc = (Math.abs(realDenormTargetPrice - realDenormPredictPrice)/realDenormTargetPrice)*100; 
          
                 System.out.println("Month = " + (k1+1) +  "  targetPrice = " + realDenormTargetPrice +
                   "  predictPrice = " + realDenormPredictPrice + "   diff = " + realDenormTargetToPredictPricePerc);
         
               }  // End for pair loop   
          
              } // End for IF  
            else
             {   
          
               vectorForRecord = Math.sqrt(
                 Math.pow(recordNormInputPriceDiffPerc_00,2) +
                 Math.pow(recordNormInputPriceDiffPerc_01,2) +
                 Math.pow(recordNormInputPriceDiffPerc_02,2) +
                 Math.pow(recordNormInputPriceDiffPerc_03,2) +
                 Math.pow(recordNormInputPriceDiffPerc_04,2) +
                 Math.pow(recordNormInputPriceDiffPerc_05,2) +
                 Math.pow(recordNormInputPriceDiffPerc_06,2) +
                 Math.pow(recordNormInputPriceDiffPerc_07,2) +
                 Math.pow(recordNormInputPriceDiffPerc_08,2) +
                 Math.pow(recordNormInputPriceDiffPerc_09,2) +
                 Math.pow(recordNormInputPriceDiffPerc_10,2) +
                 Math.pow(recordNormInputPriceDiffPerc_11,2));
           
                 // Look for the network of previous dsyd that closely matches
                 // vectorForRecord value
                       
                 minVectorValue = 999.99;
            
                 for (k3 = 0; k3 < intNumberOfSavedNetworks; k3++)
                    {
                      //if (k3 == 1060)  
                      //  r1 = r1;
                      
                      r_00 = linkToSaveInputPriceDiffPerc_00[k3];
                      r_01 = linkToSaveInputPriceDiffPerc_01[k3];
                      r_02 = linkToSaveInputPriceDiffPerc_02[k3];
                      r_03 = linkToSaveInputPriceDiffPerc_03[k3];
                      r_04 = linkToSaveInputPriceDiffPerc_04[k3];
                      r_05 = linkToSaveInputPriceDiffPerc_05[k3];
                      r_06 = linkToSaveInputPriceDiffPerc_06[k3];
                      r_07 = linkToSaveInputPriceDiffPerc_07[k3];
                      r_08 = linkToSaveInputPriceDiffPerc_08[k3];
                      r_09 = linkToSaveInputPriceDiffPerc_09[k3];
                      r_10 = linkToSaveInputPriceDiffPerc_10[k3];
                      r_11 = linkToSaveInputPriceDiffPerc_11[k3];
                                   
                      r2 = linkToSaveTargetPriceDiffPerc[k3];
                  
                      vectorForNetworkRecord = Math.sqrt(
                      Math.pow(r_00,2) +
                      Math.pow(r_01,2) +
                      Math.pow(r_02,2) +
                      Math.pow(r_03,2) +
                      Math.pow(r_04,2) +
                      Math.pow(r_05,2) +
                      Math.pow(r_06,2) +
                      Math.pow(r_07,2) +
                      Math.pow(r_08,2) +
                      Math.pow(r_09,2) +
                      Math.pow(r_10,2) +
                      Math.pow(r_11,2));
                   
                      vectorDiff = Math.abs(vectorForRecord - vectorForNetworkRecord);
                    
                      if(vectorDiff < minVectorValue)
                       {  
                         minVectorValue = vectorDiff;
                        
                         // Save this network record attributes
                         rTempKey = r_00;
                         rTempPriceDiffPerc = r2;
                         tempMinIndex = k3;
                       }
                                    
            }  // End  FOR k3 loop 
                 
            network =
            (BasicNetwork)EncogDirectoryPersistence.loadObject(new File(strSaveNetworkFileNames[tempMinIndex])); 
                   
             // Now, tempMinIndex points to the corresponding saved network
             // Load this network
             MLDataSet testingSet =
              loadCSV2Memory(strTestingFileNames[k1],intInputNeuronNumber,intOutputNeuronNumber,true,CSVFormat.ENGLISH,false);
                           
             // Get the results from the reviously saved and  now loaded network
             int i = - 1; 
      
             for (MLDataPair pair:  testingSet)
              {
                 i++;
                         
                 MLData inputData = pair.getInput();
                 MLData actualData = pair.getIdeal();
                 MLData predictData = network.compute(inputData);
        
                 // These values are Normalized as the whole input is
                 normTargetPriceDiffPerc = actualData.getData(0);
                 normPredictPriceDiffPerc = predictData.getData(0);
                 normInputPriceDiffPercFromRecord = inputData.getData(11);
  
	        // Denormalise this data to show the real result value
                 denormTargetPriceDiffPerc = ((targetPriceDiffPercDl - targetPriceDiffPercDh)*
                   normTargetPriceDiffPerc - Nh*targetPriceDiffPercDl + targetPriceDiffPercDh*Nl)/(Nl - Nh);
                 
                 denormPredictPriceDiffPerc =((targetPriceDiffPercDl - targetPriceDiffPercDh)*
                   normPredictPriceDiffPerc - Nh*targetPriceDiffPercDl + targetPriceDiffPercDh*Nl)/(Nl - Nh);
                 
                 denormInputPriceDiffPercFromRecord = ((inputPriceDiffPercDl - inputPriceDiffPercDh)*
                    normInputPriceDiffPercFromRecord - Nh*inputPriceDiffPercDl + inputPriceDiffPercDh*Nl)/(Nl - Nh);
                 
                 inputPriceFromFile = arrPrices[k1+12];
                 
                 // Convert denormPredictPriceDiffPerc and denormTargetPriceDiffPerc to a real denorm pricse 
                 realDenormTargetPrice = inputPriceFromFile + inputPriceFromFile*(denormTargetPriceDiffPerc/100);
                 realDenormPredictPrice = inputPriceFromFile + inputPriceFromFile*(denormPredictPriceDiffPerc/100);
                                        
                 realDenormTargetToPredictPricePerc = (Math.abs(realDenormTargetPrice - realDenormPredictPrice)/
                   realDenormTargetPrice)*100; 
          
                 System.out.println("Month = " + (k1+1) +  "  targetPrice = " + realDenormTargetPrice +
                   "  predictPrice = " + realDenormPredictPrice + "   diff = " + realDenormTargetToPredictPricePerc);
         
                 if (realDenormTargetToPredictPricePerc > maxGlobalResultDiff)
                  { 
                    maxGlobalResultDiff = realDenormTargetToPredictPricePerc;
                  }
        
                 sumGlobalResultDiff = sumGlobalResultDiff + realDenormTargetToPredictPricePerc; 
                
              } // End of IF
             
            }  // End for pair loop
    
            // Populate chart elements
                 
            tempMonth = (double) k1+14;
            xData.add(tempMonth);
            yData1.add(realDenormTargetPrice);
            yData2.add(realDenormPredictPrice);
            
         }   // End of loop K1
    
        // Print the max and average results
        
        System.out.println(" "); 
        System.out.println(" "); 
        System.out.println("Results of processing testing batches");  
        
        averGlobalResultDiff = sumGlobalResultDiff/intNumberOfBatchesToProcess;
                  
        System.out.println("maxGlobalResultDiff = " + maxGlobalResultDiff + "  i = " + maxGlobalIndex);
        System.out.println("averGlobalResultDiff = " + averGlobalResultDiff);
        System.out.println(" "); 
        System.out.println(" "); 
          
      }     // End of TRY
     catch (IOException e1)
      {
            e1.printStackTrace();
      } 
     
    // All testing batch files have been processed 
      XYSeries series1 = Chart.addSeries("Actual Price", xData, yData1);
      XYSeries series2 = Chart.addSeries("Forecasted Price", xData, yData2);
    
      series1.setLineColor(XChartSeriesColors.BLUE);
      series2.setMarkerColor(Color.ORANGE);
      series1.setLineStyle(SeriesLines.SOLID);
      series2.setLineStyle(SeriesLines.SOLID);
  
      // Save the chart image
      try
       {   
         BitmapEncoder.saveBitmapWithDPI(Chart, strChartFileName, BitmapFormat.JPG, 100);
       }
      catch (Exception bt)
       {
         bt.printStackTrace();
       }
        
      System.out.println ("The Chart has been saved");
      
      System.out.println("End of testing for mini-batches training");      
  
   } // End of the method
  
  
   //=============================================================
   // Load training dates file in memory
   //=============================================================
   public static void loadDatesInMemory()
    {
      BufferedReader br1 = null;
           
      DateFormat sdf = new SimpleDateFormat("yyyy-MM");
      
      Date dateTemporateDate = null;
      String strTempKeyorateDate;
      int intTemporateDate; 
      
      String line = "";
      String cvsSplitBy = ",";
 
       try
         {  
           br1 = new BufferedReader(new FileReader(strDatesTrainFileName));
           
          int i = -1;   // Inputline number
          int r = -2;   // Inputline number
          
          while ((line = br1.readLine()) != null)
           {
             i++;
             r++;
                        
            // Skip the header line
            if(i > 0)      
              {  
               // Brake the line using comma as separator
               String[] workFields = line.split(cvsSplitBy);
                              
               strTempKeyorateDate = workFields[0];
               intTemporateDate = Integer.parseInt(strTempKeyorateDate);
               
               try
                 {
                   dateTemporateDate = convertIntegerToDate(intTemporateDate);
                 }
               catch (ParseException e)
                {
                  e.printStackTrace();
                  System.exit(1);
                } 
               
               yearDateTraining[r] = dateTemporateDate;
            }
            
         }  // end of the while loop  
        
          br1.close();
         
        }
       catch (IOException ex)
        {
 	  ex.printStackTrace();
          System.err.println("Error opening files = " + ex);
          System.exit(1);
        } 
        
     }  
  
   //=============================================================
   // Convert the monthDate as integer to the Date variable
   //=============================================================
   public static Date convertIntegerToDate(int denormInputDateI) throws ParseException 
    {
     
       int numberOfYears = denormInputDateI/12;
       int numberOfMonths = denormInputDateI - numberOfYears*12;
        
       if (numberOfMonths == 0)
        { 
          numberOfYears = numberOfYears - 1; 
          numberOfMonths = 12;
        }
               
       String strNumberOfYears = Integer.toString(numberOfYears);
        
       if(numberOfMonths < 10)
         {  
           strNumberOfMonths = Integer.toString(numberOfMonths);
           strNumberOfMonths = "0" + strNumberOfMonths;
         }
        else
         {
           strNumberOfMonths = Integer.toString(numberOfMonths);
         }
  
        //strYearMonth = "01-" + strNumberOfMonths + "-" + strNumberOfYears + "T09:00:00.000Z"; 
        strYearMonth = strNumberOfYears + "-" + strNumberOfMonths; 
      
        //DateFormat sdf = new SimpleDateFormat("dd-MM-yyyy'T'HH:mm:ss.SSS'Z'");
        DateFormat sdf = new SimpleDateFormat("yyyy-MM");
        
        try
         {
          workDate = sdf.parse(strYearMonth);
         }
       catch (ParseException e)
        {
          e.printStackTrace();
        }
              
      return workDate;
      
   }  // End of method  
   
   
   //===================================================================================
   // Convert the monthDate as integer to the string strDate variable
   //===================================================================================
   public static String convertIntegerToString(int denormInputDateI)  
    {
       int numberOfYears = denormInputDateI/12;
       int numberOfMonths = denormInputDateI - numberOfYears*12;
        
       if (numberOfMonths == 0)
        { 
          numberOfYears = numberOfYears - 1; 
          numberOfMonths = 12;
        }
     
       String strNumberOfYears = Integer.toString(numberOfYears);
        
       if(numberOfMonths < 10)
         {  
           strNumberOfMonths = Integer.toString(numberOfMonths);
           strNumberOfMonths = "0" + strNumberOfMonths;
         }
        else
         {
           strNumberOfMonths = Integer.toString(numberOfMonths);
         }
  
        strYearMonth = strNumberOfYears + "-" + strNumberOfMonths; 
          
      return strYearMonth;
      
   }  // End of method 
   
   //=============================================================
   // Load Prices file in memory
   //=============================================================
   public static void loadPriceFileInMemory()
    {
      BufferedReader br1 = null;
      
      String line = "";
      String cvsSplitBy = ",";
      String strTempKeyPrice = "";
      double tempPrice = 0.00;
      
       try
         {  
           br1 = new BufferedReader(new FileReader(strPricesFileName));
           
          int i = -1;   // Inputline number
          int r = -2;   // Inputline number
          
          while ((line = br1.readLine()) != null)
           {
             i++;
             r++;
                        
            // Skip the header line
            if(i > 0)      
              {  
               // Brake the line using comma as separator
               String[] workFields = line.split(cvsSplitBy);
                              
               strTempKeyPrice = workFields[0];
               tempPrice = Double.parseDouble(strTempKeyPrice);
               arrPrices[r] = tempPrice;
              
               //System.out.println("strYearDateTraing[i] = " + strYearDateTraing[i]);
            }
            
         }  // end of the while loop  
        
          br1.close();
         
        }
       catch (IOException ex)
        {
 	  ex.printStackTrace();
          System.err.println("Error opening files = " + ex);
          System.exit(1);
        } 
        
     } 
   
 } // End of the  Encog class
