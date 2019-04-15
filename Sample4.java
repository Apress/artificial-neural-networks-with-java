//==============================================================================
//  Approximation of the complex periodic function. The input is a training
//  or testing file with the records built as sliding windows. Each sliding
//  window record contains 11 fields. 
//  The first 10 fields are the field1 values from the original 10 records plus
//  the field2 value from the next record, which is actually the difference
//  between the target values of the next original record (record 11) and
//  record 10.
//==============================================================================

package sample4;

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

public class Sample4 implements ExampleChart<XYChart>
{
   static double doublePointNumber = 0.00; 
   static int intPointNumber = 0;  
   static InputStream input = null;
   static double[] arrFunctionValue = new double[500]; 
   static double inputDiffValue = 0.00; 
   static double targetDiffValue = 0.00;
   static double predictDiffValue = 0.00;
   static double valueDifferencePerc = 0.00;
   static String strFunctionValuesFileName;
   static int returnCode  = 0;
   static int numberOfInputNeurons;
   static int numberOfOutputNeurons;
   static int numberOfRecordsInFile;
   static int intNumberOfRecordsInTestFile;
   static double realTargetDiffValue;
   static double realPredictDiffValue;
    static String functionValuesTrainFileName;
   static String functionValuesTestFileName;
   static String trainFileName;
   static String priceFileName;
   static String testFileName;
   static String chartTrainFileName;
   static String chartTestFileName;
   static String networkFileName;
   static int workingMode;
   static String cvsSplitBy = ",";
   static double Nh;  
   static double Nl;  
   static double Dh; 
   static double Dl;
   static String inputtargetFileName       ;
   static double lastFunctionValueForTraining = 0.00;
   static int tempIndexField;
   static double tempTargetField;
   static  int[] arrIndex = new int[100];
   static double[] arrTarget = new double[100];
   
   static List<Double> xData = new ArrayList<Double>();
   static List<Double> yData1 = new ArrayList<Double>(); 
   static List<Double> yData2 = new ArrayList<Double>();  
   
   static XYChart Chart;
   
  @Override
  public XYChart getChart()
   {
    
    // Create Chart
    
    Chart = new  XYChartBuilder().width(900).height(500).title(getClass().
              getSimpleName()).xAxisTitle("Days").yAxisTitle("y= f(x)").build();
    
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
    Chart.getStyler().setLegendPosition(LegendPosition.OutsideS);
    Chart.getStyler().setLegendSeriesLineLength(12);
    Chart.getStyler().setAxisTitleFont(new Font(Font.SANS_SERIF, Font.ITALIC, 18));
    Chart.getStyler().setAxisTickLabelsFont(new Font(Font.SERIF, Font.PLAIN, 11));
    Chart.getStyler().setDatePattern("yyyy-MM");
    Chart.getStyler().setDecimalPattern("#0.00");
    //Chart.getStyler().setLocale(Locale.GERMAN);   
    
    // Interval to normalize
    double Nh =  1;  
    double Nl = -1;  
   
    // Values in the sliding windows
    double Dh = 50.00; 
    double Dl = -20.00;
    
    try
       {
          // Configuration
    
         // Training mode
         workingMode = 1; 
         trainFileName = "C:/Book_Examples/Sample4_Norm_Train_Sliding_Windows_File.csv";
         functionValuesTrainFileName = "C:/Book_Examples/Sample4_Function_values_Period_1.csv";
         chartTrainFileName = "XYLine_Sample4_Train_Chart";   
         numberOfRecordsInFile = 51;
        
         // Testing mode
         //workingMode = 2; 
         //trainFileName = "C:/Book_Examples/Sample4_Norm_Train_Sliding_Windows_File.csv";
         //functionValuesTrainFileName = "C:/Book_Examples/Sample4_Function_values_Period_1.csv";
         //chartTestFileName = "XYLine_Sample4_Test_Chart";
         //numberOfRecordsInFile = 51; 
         //lastFunctionValueForTraining = 100.00;
            
         // Common configuration
         networkFileName = "C:/Book_Examples/Example4_Saved_Network_File.csv";
         inputtargetFileName = "C:/Book_Examples/Sample4_Input_File.csv";
         numberOfInputNeurons = 10;
         numberOfOutputNeurons = 1;
     
         // Load training file in memory
         loadInputTargetFileInMemory();      
         
         // Check the working mode to run
         
         if(workingMode == 1)
          {
            // Training mode. 
            
             File file1 = new File(chartTrainFileName);
             File file2 = new File(networkFileName);
        
             if(file1.exists())
               file1.delete();    
         
             if(file2.exists())
               file2.delete();  
            
             returnCode = 0;    // Clear the error Code
         
             do
              { 
                returnCode = trainValidateSaveNetwork();
                
              } while (returnCode > 0);
                
          }   // End the train logic        
         
                  
         else
          { 
            // Test mode. 
                   
            File file1 = new File(chartTestFileName);
        
            if(file1.exists())
              file1.delete(); 
            
            loadAndTestNetwork();
      
          }
         
         
       }
      catch (NumberFormatException e)
       {
           System.err.println("Problem parsing workingMode. workingMode = " + workingMode);
           System.exit(1);
        }
      catch (Throwable t)
        {
   	  t.printStackTrace();
          System.exit(1);
   	} 
       finally 
        {
          Encog.getInstance().shutdown();
   	} 
 
    Encog.getInstance().shutdown();
          
    return Chart;
    
   }  // End of the method 
      
  
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
      ExampleChart<XYChart> exampleChart = new Sample4();
      XYChart Chart = exampleChart.getChart();
      new SwingWrapper<XYChart>(Chart).displayChart();  
    } // End of the main method
   
   
   //==========================================================================
   // This method trains, Validates, and saves the trained network file
   //==========================================================================
   static public int trainValidateSaveNetwork()
    {
      double functionValue = 0.00; 
      
      double denormInputValueDiff = 0.00;
      double denormTargetValueDiff = 0.00;
      double denormPredictValueDiff = 0.00;
      
      // Load the training CSV file in memory
      MLDataSet trainingSet = 
        loadCSV2Memory(trainFileName,numberOfInputNeurons,numberOfOutputNeurons,
          true,CSVFormat.ENGLISH,false);  
                  
      // create a neural network
      BasicNetwork network = new BasicNetwork();
     
      // Input layer
      network.addLayer(new BasicLayer(null,true,10));
      
      // Hidden layer
      network.addLayer(new BasicLayer(new ActivationTANH(),true,13));
      network.addLayer(new BasicLayer(new ActivationTANH(),true,13));
      network.addLayer(new BasicLayer(new ActivationTANH(),true,13));
      network.addLayer(new BasicLayer(new ActivationTANH(),true,13));
                  
      // Output layer
      network.addLayer(new BasicLayer(new ActivationTANH(),false,1));
          
      network.getStructure().finalizeStructure();
      network.reset();
                  		
      // train the neural network
      final ResilientPropagation train = new ResilientPropagation(network, trainingSet);
      //Backpropagation train = new Backpropagation(network,trainingSet,0.7,0.3);
      //Backpropagation train = new Backpropagation(network,trainingSet,0.5,0.5);
      
      int epoch = 1;

      returnCode = 0;
            
      do
       {
	 train.iteration();
	 System.out.println("Epoch #" + epoch + " Error:" + train.getError());
	  
         epoch++;
         
         if (epoch >= 11000 && network.calculateError(trainingSet) > 0.00000119)    // 0.0119   0.035
             { 
              returnCode = 1;
              
              System.out.println("Error = " + network.calculateError(trainingSet));  
              System.out.println("Try again");  
              return returnCode; 
             }       
         
       } while(train.getError() > 0.000001187); //   0.01187   0.03
      // } while (network.calculateError(trainingSet) > 0.000020115);   // 0.00002011    0.00008
         
      // Save the network file
      EncogDirectoryPersistence.saveObject(new File(networkFileName),network);
      
      System.out.println("Neural Network Results:");
      
      double sumGlobalDifferencePerc = 0.00;
      double averGlobalDifferencePerc = 0.00;
      double maxGlobalDifferencePerc = 0.00;
      int m = 0; 
      double xPoint_Initial = 1.00;
      double xPoint_Increment = 1.00;
      double xPoint = xPoint_Initial - xPoint_Increment;
      
      realTargetDiffValue = 0.00;
      realPredictDiffValue = 0.00;
      
      for(MLDataPair pair: trainingSet)
        {
            m++;  
            xPoint = xPoint + xPoint_Increment; 
            
            if(xPoint >  50.00) 
               break;
                  
             final MLData output = network.compute(pair.getInput());
          
             MLData inputData = pair.getInput();
             MLData actualData = pair.getIdeal();
             MLData predictData = network.compute(inputData);
        
             // Calculate and print the results
             inputDiffValue = inputData.getData(0); 
             targetDiffValue = actualData.getData(0);
             predictDiffValue = predictData.getData(0);
           
             // Denormalize the values
             denormInputValueDiff     =((Dl - Dh)*inputDiffValue - Nh*Dl + Dh*Nl)/(Nl - Nh);
             denormTargetValueDiff = ((Dl - Dh)*targetDiffValue - Nh*Dl + Dh*Nl)/(Nl - Nh);
             denormPredictValueDiff =((Dl - Dh)*predictDiffValue - Nh*Dl + Dh*Nl)/(Nl - Nh);
             
             functionValue = arrFunctionValue[m-1];
             
             realTargetDiffValue = functionValue + denormTargetValueDiff;
             realPredictDiffValue = functionValue + denormPredictValueDiff;
             valueDifferencePerc = 
               Math.abs(((realTargetDiffValue - realPredictDiffValue)/realPredictDiffValue)*100.00);
          
             
             System.out.println ("xPoint = " + xPoint + "  realTargetDiffValue = " + realTargetDiffValue + 
               "  realPredictDiffValue = " + realPredictDiffValue);
            
             sumGlobalDifferencePerc = sumGlobalDifferencePerc + valueDifferencePerc;
                     
             if (valueDifferencePerc > maxGlobalDifferencePerc)
               maxGlobalDifferencePerc = valueDifferencePerc; 
             
                          
             xData.add(xPoint);
             yData1.add(realTargetDiffValue);
             yData2.add(realPredictDiffValue);
       
        }   // End for pair loop

        XYSeries series1 = Chart.addSeries("Actual data", xData, yData1);
        XYSeries series2 = Chart.addSeries("Predict data", xData, yData2);
    
        series1.setLineColor(XChartSeriesColors.BLUE);
        series2.setMarkerColor(Color.ORANGE);
        series1.setLineStyle(SeriesLines.SOLID);
        series2.setLineStyle(SeriesLines.SOLID);
        
        try
         {   
           //Save the chart image
           BitmapEncoder.saveBitmapWithDPI(Chart, chartTrainFileName, BitmapFormat.JPG, 100);
           System.out.println ("Train Chart file has been saved") ;  
         }
       catch (IOException ex)
        {
 	 ex.printStackTrace();
         System.exit(3);
        } 
        
        // Finally, save this trained network
        EncogDirectoryPersistence.saveObject(new File(networkFileName),network);
        System.out.println ("Train Network has been saved") ;  
 
        averGlobalDifferencePerc  = sumGlobalDifferencePerc/numberOfRecordsInFile;
        
        System.out.println(" ");
        System.out.println("maxGlobalDifferencePerc = " + maxGlobalDifferencePerc + "  averGlobalDifferencePerc = " + averGlobalDifferencePerc);
  
        returnCode = 0;
        return returnCode;
        
    }   // End of the method


   
   //==========================================================================
   // Test Metod
   //==========================================================================
   static public void loadAndTestNetwork()
    { 
     System.out.println("Testing the networks results");
     
     List<Double> xData = new ArrayList<Double>();
     List<Double> yData1 = new ArrayList<Double>();   
     List<Double> yData2 = new ArrayList<Double>(); 
     
     double sumGlobalDifferencePerc = 0.00;
     double maxGlobalDifferencePerc = 0.00;
     double averGlobalDifferencePerc = 0.00;
         
     double denormInputValueDiff = 0.00;
     double denormTargetValueDiff = 0.00;
     double denormPredictValueDiff = 0.00;
     double functionValue;
     
     BufferedReader br4; 
     BasicNetwork network;
           
     // Process testing records
     maxGlobalDifferencePerc  = 0.00;
     averGlobalDifferencePerc = 0.00;
     sumGlobalDifferencePerc = 0.00; 
     realTargetDiffValue = 0.00;
     realPredictDiffValue = 0.00;
        
     // Load the training dataset into memory
     MLDataSet trainingSet =
     loadCSV2Memory(trainFileName,numberOfInputNeurons,numberOfOutputNeurons,true,
       CSVFormat.ENGLISH,false);
       
     // Load the saved trained network
     network =
       (BasicNetwork)EncogDirectoryPersistence.loadObject(new File(networkFileName)); 
  
     int m = 0;  
     
     // Record number in the input file
     double xPoint_Initial = 51.00;
     double xPoint_Increment = 1.00;
     double xPoint = xPoint_Initial - xPoint_Increment;
               
     for (MLDataPair pair:  trainingSet)
      {
        m++;  
        xPoint = xPoint + xPoint_Increment; 
                     
        final MLData output = network.compute(pair.getInput());
          
        MLData inputData = pair.getInput();
        MLData actualData = pair.getIdeal();
        MLData predictData = network.compute(inputData);
        
        // Calculate and print the results
        inputDiffValue = inputData.getData(0); 
        targetDiffValue = actualData.getData(0);
        predictDiffValue = predictData.getData(0); 
      
        if(m == 1)
          functionValue = lastFunctionValueForTraining;  
        else
         functionValue = realPredictDiffValue;
       
        // Denormalize the values
        denormInputValueDiff     =((Dl - Dh)*inputDiffValue - Nh*Dl + Dh*Nl)/(Nl - Nh);
        denormTargetValueDiff = ((Dl - Dh)*targetDiffValue - Nh*Dl + Dh*Nl)/(Nl - Nh);
        denormPredictValueDiff =((Dl - Dh)*predictDiffValue - Nh*Dl + Dh*Nl)/(Nl - Nh);
   
        realTargetDiffValue = functionValue +  denormTargetValueDiff;
        realPredictDiffValue = functionValue + denormPredictValueDiff;
        valueDifferencePerc = 
          Math.abs(((realTargetDiffValue - realPredictDiffValue)/realPredictDiffValue)*100.00);
        
                
        System.out.println ("xPoint = " + xPoint + "  realTargetDiffValue = " +
          realTargetDiffValue + "  realPredictDiffValue = " + realPredictDiffValue);
             
        sumGlobalDifferencePerc = sumGlobalDifferencePerc + valueDifferencePerc;
        
        if (valueDifferencePerc > maxGlobalDifferencePerc)
          maxGlobalDifferencePerc = valueDifferencePerc; 
                  
        xData.add(xPoint);
        yData1.add(realTargetDiffValue);
        yData2.add(realPredictDiffValue);  
              
       }  // End for pair loop
    
      // Print the max and average results
       System.out.println(" ");  
      averGlobalDifferencePerc = sumGlobalDifferencePerc/numberOfRecordsInFile;
                  
      System.out.println("maxErrorDifferencePercent = " + maxGlobalDifferencePerc);
      System.out.println("averErrorDifferencePercent = " + averGlobalDifferencePerc);
   
      // All testing batch files have been processed 
      XYSeries series1 = Chart.addSeries("Actual", xData, yData1);
      XYSeries series2 = Chart.addSeries("Predicted", xData, yData2);
    
      series1.setLineColor(XChartSeriesColors.BLUE);
      series2.setMarkerColor(Color.ORANGE);
      series1.setLineStyle(SeriesLines.SOLID);
      series2.setLineStyle(SeriesLines.SOLID);
  
      // Save the chart image
      try
       {   
         BitmapEncoder.saveBitmapWithDPI(Chart, chartTestFileName , BitmapFormat.JPG, 100);
       }
      catch (Exception bt)
       {
         bt.printStackTrace();
       }
        
      System.out.println ("The Chart has been saved");
      System.out.println("End of testing for test records");      
  
   } // End of the method
   
   
   //===============================================================
   // Load Function values for training file in memory
   //===============================================================
   public static void loadFunctionValueTrainFileInMemory()
    {
      BufferedReader br1 = null;
      
      String line = "";
      String cvsSplitBy = ",";
      String tempXPointValue = "";
      double tempYFunctionValue = 0.00;
      
       try
         {  
           br1 = new BufferedReader(new FileReader(functionValuesTrainFileName));
           
          int i = -1;   
          int r = -2;   
          
          while ((line = br1.readLine()) != null)
           {
             i++;
             r++;
          
            // Skip the header line
            if(i > 0)      
              {  
               // Brake the line using comma as separator
               String[] workFields = line.split(cvsSplitBy);
               
               tempYFunctionValue = Double.parseDouble(workFields[0]);
               arrFunctionValue[r] = tempYFunctionValue;
              
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
 
   
   //===============================================================
   // Load Sample4_Input_File into 2 arrays in memory
   //===============================================================
   public static void loadInputTargetFileInMemory()
    {
      BufferedReader br1 = null;
      
      String line = "";
      String cvsSplitBy = ",";
      
       try
         {  
           br1 = new BufferedReader(new FileReader(inputtargetFileName       ));
           
          int i = -1;   
          int r = -2;   
          
          while ((line = br1.readLine()) != null)
           {
             i++;
             r++;
             
            if(i == 50)
             i = i;   
                
            // Skip the header line
            if(i > 0)      
              {  
                // Brake the line using comma as separator
                String[] workFields = line.split(cvsSplitBy);
                            
                tempTargetField = Double.parseDouble(workFields[1]);
               
                arrIndex[r] =  r;
                arrTarget[r] = tempTargetField;
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
   
} // End of the class
