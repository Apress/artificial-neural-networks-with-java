// ============================================================================
// Approximation of the periodic function outside of the training range.
//
// The input is the file consisting of records with two fields:
// - The first field holds the difference between the function values of the
// current and first records. 
// - The second field holds the difference between the function values of the
// next and current records. 
// ============================================================================

package sample3b;
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

/**
 *
 * @author i262666
 */
public class Sample3b implements ExampleChart<XYChart>
{
   
    static double Nh =  1;  
    static double Nl = -1;  
   
   // First column
   static double maxXPointDh = 1.35; 
   static double minXPointDl = 0.10;
   
   // Second column - target data
   static double maxTargetValueDh = 1.35; 
   static double minTargetValueDl = 0.10; 
   
   static double doublePointNumber = 0.00; 
   static int intPointNumber = 0;  
   static InputStream input = null;
   static double[] arrFunctionValue = new double[500]; 
   static double inputDiffValue = 0.00; 
   static double predictDiffValue = 0.00;
   static double targetDiffValue = 0.00;
   static double valueDifferencePerc = 0.00;
    static String strFunctionValuesFileName;
   static int returnCode  = 0;
   static int numberOfInputNeurons;
   static int numberOfOutputNeurons;
   static int numberOfRecordsInFile;
   static int intNumberOfRecordsInTestFile;
   static double realTargetValue               ;
   static double realPredictValue         ;
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
   static double denormTargetDiffPerc;
   static double denormPredictDiffPerc;
   
   static List<Double> xData = new ArrayList<Double>();
   static List<Double> yData1 = new ArrayList<Double>(); 
   static List<Double> yData2 = new ArrayList<Double>();  
   
   static XYChart Chart;
   
  @Override
  public XYChart getChart()
   {
    
    // Create Chart
    
    Chart = new  XYChartBuilder().width(900).height(500).title(getClass().
              getSimpleName()).xAxisTitle("x").yAxisTitle("y= f(x)").build();
    
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
    Chart.getStyler().setLegendPosition(LegendPosition.InsideSE);
    Chart.getStyler().setLegendSeriesLineLength(12);
    Chart.getStyler().setAxisTitleFont(new Font(Font.SANS_SERIF, Font.ITALIC, 18));
    Chart.getStyler().setAxisTickLabelsFont(new Font(Font.SERIF, Font.PLAIN, 11));
    Chart.getStyler().setDatePattern("yyyy-MM");
    Chart.getStyler().setDecimalPattern("#0.00");
           
    
               
    // Configuration
    
    // Train 
    //workingMode = 1; 
    //trainFileName = "C:/My_Neural_Network_Book/Book_Examples/Sample3b_Norm_Tan_Train.csv";
    //functionValuesTrainFileName = "C:/My_Neural_Network_Book/Book_Examples/Sample3b_Tan_Calculate_Train.csv";
    //chartTrainFileName = "C:/My_Neural_Network_Book/Book_Examples/Sample3b_XYLine_Tan_Train_Chart";   
    //numberOfRecordsInFile = 12;
      
    // Test 
    workingMode = 2; 
    testFileName = "C:/My_Neural_Network_Book/Book_Examples/Sample3b_Norm_Tan_Test.csv";
    functionValuesTestFileName = "C:/My_Neural_Network_Book/Book_Examples/Sample3b_Tan_Calculate_Test.csv";
    chartTestFileName = "C:/My_Neural_Network_Book/Book_Examples/Sample3b_XYLine_Tan_Test_Chart";
    numberOfRecordsInFile = 12;
            
    // Common configuration
    networkFileName = "C:/My_Neural_Network_Book/Book_Examples/Sample3b_Saved_Tan_Network_File.csv";
    numberOfInputNeurons = 1;
    numberOfOutputNeurons = 1;
    
    try
     {   
         // Check the working mode to run
          
         if(workingMode == 1)
          {
            // Train mode
            loadFunctionValueTrainFileInMemory();    
              
             File file1 = new File(chartTrainFileName);
             File file2 = new File(networkFileName);
        
             if(file1.exists())
               file1.delete();    
         
             if(file2.exists())
               file2.delete();  
            
             returnCode = 0;    // Clear the return code variable
         
             do
              { 
                returnCode = trainValidateSaveNetwork();
                
              } while (returnCode > 0);
                
          }   // End the train logic        
         else
          { 
            // Testing mode. 
           
            // Load testing file in memory
            loadTestFileInMemory();   
              
            File file1 = new File(chartTestFileName);
        
            if(file1.exists())
              file1.delete(); 
            
            loadAndTestNetwork();
      
          }  
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
      ExampleChart<XYChart> exampleChart = new Sample3b();
      XYChart Chart = exampleChart.getChart();
      new SwingWrapper<XYChart>(Chart).displayChart();  
    } // End of the main method
   
   
   //==========================================================================
   // This method trains, Validates, and saves the trained network file
   //==========================================================================
   static public int trainValidateSaveNetwork()
    {
      double functionValue = 0.00; 

      // Load the training CSV file in memory
      MLDataSet trainingSet = 
        loadCSV2Memory(trainFileName,numberOfInputNeurons,numberOfOutputNeurons,
          true,CSVFormat.ENGLISH,false);  
                  
      // create a neural network
      BasicNetwork network = new BasicNetwork();
     
      // Input layer
      network.addLayer(new BasicLayer(null,true,1));
      
      // Hidden layer
      network.addLayer(new BasicLayer(new ActivationTANH(),true,5));
      network.addLayer(new BasicLayer(new ActivationTANH(),true,5));
      network.addLayer(new BasicLayer(new ActivationTANH(),true,5));
      //network.addLayer(new BasicLayer(new ActivationTANH(),true,5));
      //network.addLayer(new BasicLayer(new ActivationTANH(),true,5));
      //network.addLayer(new BasicLayer(new ActivationTANH(),true,5));
      //network.addLayer(new BasicLayer(new ActivationTANH(),true,5));
      //network.addLayer(new BasicLayer(new ActivationTANH(),true,5));
      //network.addLayer(new BasicLayer(new ActivationTANH(),true,5));
      //network.addLayer(new BasicLayer(new ActivationTANH(),true,5));
           
      // Output layer
      network.addLayer(new BasicLayer(new ActivationTANH(),false,1));
          
      network.getStructure().finalizeStructure();
      network.reset();
                  		
      // train the neural network
      final ResilientPropagation train = new ResilientPropagation(network, trainingSet);
            
      int epoch = 1;
      returnCode = 0;
            
      do
       {
	 train.iteration();
	 System.out.println("Epoch #" + epoch + " Error:" + train.getError());
	  
         epoch++;
         
         if (epoch >= 500 && network.calculateError(trainingSet) > 0.000000061)    // 0.00049591   0.00008
             { 
              returnCode = 1;
                              
              System.out.println("Try again");  
              return returnCode; 
             }       
         
       } while(train.getError() > 0.00000006); //   0.0004959   0.00008
               
      // Save the network file
      EncogDirectoryPersistence.saveObject(new File(networkFileName),network);
      
      System.out.println("Neural Network Results:");
      
      double sumDifferencePerc = 0.00;
      double averNormDifferencePerc = 0.00;
      double maxErrorPerc = 0.00;
      
      int m = -1;                  // Record number in the input file
      double xPoint_Initial = 0.00;
      double xPoint_Increment = 0.12;
      //double xPoint = xPoint_Initial - xPoint_Increment;
      double xPoint = xPoint_Initial;
            
      realTargetValue = 0.00;
      realPredictValue = 0.00;
      
      //System.out.println ("xPoint = " + xPoint_Initial + "  realTargetValue = " + realTargetValue                + "  realPredictValue          = " + realPredictValue          + "  valueDifferencePerc = " + valueDifferencePerc);
      
      for(MLDataPair pair: trainingSet)
        {
            m++;  
            xPoint = xPoint + xPoint_Increment; 
            
            //if(xPoint >  3.14) 
            //   break;
                  
             final MLData output = network.compute(pair.getInput());
          
             MLData inputData = pair.getInput();
             MLData actualData = pair.getIdeal();
             MLData predictData = network.compute(inputData);
        
             // Calculate and print the results
             inputDiffValue = inputData.getData(0); 
             targetDiffValue = actualData.getData(0);
             predictDiffValue = predictData.getData(0);
             
             // denormalize the values
             denormTargetDiffPerc = ((minXPointDl - maxXPointDh)*targetDiffValue - Nh*minXPointDl + maxXPointDh*Nl)/(Nl - Nh);
             denormPredictDiffPerc =((minTargetValueDl - maxTargetValueDh)*predictDiffValue - Nh*minTargetValueDl + maxTargetValueDh*Nl)/(Nl - Nh);
             
             functionValue = arrFunctionValue[m+1];
             
             realTargetValue = functionValue + denormTargetDiffPerc;
             realPredictValue = functionValue + denormPredictDiffPerc;
             
             valueDifferencePerc = 
               Math.abs(((realTargetValue - realPredictValue)/realPredictValue)*100.00);
          
             System.out.println ("xPoint = " + xPoint + "  realTargetValue = " + 
               denormTargetDiffPerc + "  realPredictValue = " + denormPredictDiffPerc + "  valueDifferencePerc = " + valueDifferencePerc);
             
             sumDifferencePerc = sumDifferencePerc + valueDifferencePerc;
        
             if (valueDifferencePerc > maxErrorPerc && m > 0)
               maxErrorPerc = valueDifferencePerc; 
             
             //realTargetValue = arrFunctionValue[m];
             
             xData.add(xPoint);
             yData1.add(denormTargetDiffPerc);
             yData2.add(denormPredictDiffPerc);
       
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
 
        averNormDifferencePerc  = sumDifferencePerc/numberOfRecordsInFile;
        
        System.out.println(" ");
        System.out.println("maxErrorPerc = " + maxErrorPerc + 
          "  averNormDifferencePerc = " + averNormDifferencePerc);
  
        returnCode = 0;
        
        return returnCode;
        
    }   // End of the method

   
   //==========================================================================
   // This method load and test the trained network at the points not
   // used for training.
   //==========================================================================
   static public void loadAndTestNetwork()
    { 
     System.out.println("Testing the networks results");
     
     List<Double> xData = new ArrayList<Double>();
     List<Double> yData1 = new ArrayList<Double>();   
     List<Double> yData2 = new ArrayList<Double>(); 
     
     double sumDifferencePerc = 0.00;
     double maxErrorPerc = 0.00;
     double maxGlobalResultDiff = 0.00;
     double averErrorPerc = 0.00;
     double sumGlobalResultDiff = 0.00; 
     double functionValue;
     
     BufferedReader br4; 
     BasicNetwork network;
     int k1 = 0;
       
     // Process test records
     maxGlobalResultDiff = 0.00;
     averErrorPerc = 0.00;
     sumGlobalResultDiff = 0.00;  
        
     // Load the test dataset into memory
     //MLDataSet testingSet =
     //loadCSV2Memory(testFileName,numberOfInputNeurons,numberOfOutputNeurons,true,
     //  CSVFormat.ENGLISH,false);
     
     
     MLDataSet testingSet =
     loadCSV2Memory(testFileName,numberOfInputNeurons,numberOfOutputNeurons,true,
       CSVFormat.ENGLISH,false);
     
     // Load the saved trained network
     network =
       (BasicNetwork)EncogDirectoryPersistence.loadObject(new File(networkFileName)); 
  
     int i = - 1; // Index of the current record
     int m = -1;
     
     double xPoint_Initial = 3.141592654;
     double xPoint_Increment = 0.12;
     //double xPoint = xPoint_Initial - xPoint_Increment;
     double xPoint = xPoint_Initial;
     
     realTargetValue = 0.00;
     realPredictValue = 0.00;
               
     for (MLDataPair pair:  testingSet)
      {
        m++;  
        xPoint = xPoint + xPoint_Increment; 
            
        //if(xPoint > 9.40) 
        //   break;
                  
        final MLData output = network.compute(pair.getInput());
          
        MLData inputData = pair.getInput();
        MLData actualData = pair.getIdeal();
        MLData predictData = network.compute(inputData);
        
        // Calculate and print the results
        inputDiffValue = inputData.getData(0); 
        targetDiffValue = actualData.getData(0);
        predictDiffValue = predictData.getData(0); 
        
        // denormalize the values
        denormTargetDiffPerc = ((minXPointDl - maxXPointDh)*targetDiffValue - Nh*minXPointDl + maxXPointDh*Nl)/(Nl - Nh);
        denormPredictDiffPerc =((minTargetValueDl - maxTargetValueDh)*predictDiffValue - Nh*minTargetValueDl + maxTargetValueDh*Nl)/(Nl - Nh);
             
        functionValue = arrFunctionValue[m+1];
             
        realTargetValue = functionValue + denormTargetDiffPerc;
        realPredictValue = functionValue + denormPredictDiffPerc;
             
        valueDifferencePerc = 
           Math.abs(((realTargetValue - realPredictValue)/realPredictValue)*100.00);
          
        System.out.println ("xPoint = " + xPoint + "  realTargetValue = " + 
        realTargetValue + "  realPredictValue = " + realPredictValue + "  valueDifferencePerc = " + valueDifferencePerc);
             
        sumDifferencePerc = sumDifferencePerc + valueDifferencePerc;
        
        if (valueDifferencePerc > maxErrorPerc && m > 0)
          maxErrorPerc = valueDifferencePerc; 
                    
        xData.add(xPoint);
        yData1.add(realTargetValue);
        yData2.add(realPredictValue);
       
                      
      }  // End for pair loop
    
      // Print max and average results
        
      System.out.println(" ");  
      averErrorPerc = sumDifferencePerc/numberOfRecordsInFile;
                  
      System.out.println("maxErrorPerc = " + maxErrorPerc);
      System.out.println("averErrorPerc = " + averErrorPerc);
   
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
    
   } // End of the method



   //==================================================================
   // Load Training Function Values file in memory
   //==================================================================
   public static void loadFunctionValueTrainFileInMemory()
    {
      BufferedReader br1 = null;
      
      String line = "";
      String cvsSplitBy = ",";
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
             
            if(i == 201)
             i = i;   
                
            // Skip the header line
            if(i > 0)      
              {  
               // Brake the line using comma as separator
               String[] workFields = line.split(cvsSplitBy);
               
               tempYFunctionValue = Double.parseDouble(workFields[1]);
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
 
   
   //==================================================================
   // Load testing Function Values file in memory
   //==================================================================
   public static void loadTestFileInMemory()
    {
      BufferedReader br1 = null;
      
      String line = "";
      String cvsSplitBy = ",";
      double tempYFunctionValue = 0.00;
      
       try
         {  
           br1 = new BufferedReader(new FileReader(functionValuesTestFileName));
           
          int i = -1;   // Inputline number
          int r = -2;   // Inputline number
          
          while ((line = br1.readLine()) != null)
           {
             i++;
             r++;
             
            if(i == 201)
             i = i;   
                
            // Skip the header line
            if(i > 0)      
              {  
                // Brake the line using comma as separator
                String[] workFields = line.split(cvsSplitBy);
               
                tempYFunctionValue = Double.parseDouble(workFields[1]);
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
 
} // End of the class
