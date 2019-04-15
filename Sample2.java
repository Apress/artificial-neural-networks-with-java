// ============================================================================
// Approximate the single-variable fuction which values are given at 9 points.
// The input train/test files are normalized. 
// ============================================================================

package sample2;

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

public class Sample2 implements ExampleChart<XYChart>
{
    // Interval to normalize
   static double Nh =  1;  
   static double Nl = -1;  
   
   // First column
   static double minXPointDl = 0.00;
   static double maxXPointDh = 5.00; 
   
   // Second column - target data
   static double minTargetValueDl = 0.00; 
   static double maxTargetValueDh = 5.00; 
   
   static double doublePointNumber = 0.00; 
   static int intPointNumber = 0;  
   static InputStream input = null;
   static int intNumberOfRecordsInTrainFile;
   static double[] arrPrices = new double[2500]; 
   static double normInputXPointValue = 0.00; 
   static double normPredictXPointValue = 0.00;
   static double normTargetXPointValue = 0.00;
   static double normDifferencePerc = 0.00;
   static double denormInputXPointValue = 0.00;
   static double denormPredictXPointValue = 0.00;
   static double denormTargetXPointValue = 0.00;
   static double valueDifference = 0.00;
   static int returnCode  = 0;
   static int numberOfInputNeurons;
   static int numberOfOutputNeurons;
   static int intNumberOfRecordsInTestFile;
   static String trainFileName;
   static String priceFileName;
   static String testFileName;
   static String chartTrainFileName;
   static String chartTestFileName;
   static String networkFileName;
   static int workingMode;
   static String cvsSplitBy = ",";
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
    //Chart.getStyler().setLocale(Locale.GERMAN);   
       
    try
       {
         // Configuration (comment and uncomment the appropriate configuration)
           
         
         // Config for training the network
         //workingMode = 1; 
         //intNumberOfRecordsInTrainFile = 10;
         //trainFileName = "C:/My_Neural_Network_Book/Book_Examples/Sample2_Train_Norm.csv";   
         //chartTrainFileName = "Sample2_XYLine_Train_Results_Chart";   
         
         // Config for testing the trained network
         workingMode = 2; 
         intNumberOfRecordsInTestFile = 10;
         testFileName = "C:/My_Neural_Network_Book/Book_Examples/Sample2_Test_Norm.csv";    
         chartTestFileName = "XYLine_Test_Results_Chart";   
         
         // Common configuration data
         networkFileName = "C:/Book_Examples/Sample2_Saved_Network_File.csv";
         numberOfInputNeurons = 1;
         numberOfOutputNeurons = 1;
         
         // Check the working mode to run
         
         // Training mode. 
         if(workingMode == 1)
          {
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
            
           }           
         
        // Test mode. 
        if(workingMode == 2)
         { 
           // Test using the test dataset as input  
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
      
  
   //--------------------------------------------------------------
   // Load CSV to memory.
   // @return The loaded dataset.
   // -------------------------------------------------------------
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
      ExampleChart<XYChart> exampleChart = new Sample2();
      XYChart Chart = exampleChart.getChart();
      new SwingWrapper<XYChart>(Chart).displayChart();  
    } // End of the main method
   
   
   //==========================================================================
   // Training method. Train, validate, and save the trained network file
   //==========================================================================
   static public int trainValidateSaveNetwork()
    {
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
      network.addLayer(new BasicLayer(new ActivationTANH(),true,5));
      network.addLayer(new BasicLayer(new ActivationTANH(),true,5));
      network.addLayer(new BasicLayer(new ActivationTANH(),true,5));
      network.addLayer(new BasicLayer(new ActivationTANH(),true,5));
      
      //network.addLayer(new BasicLayer(new ActivationSigmoid(),true,3));
      //network.addLayer(new BasicLayer(new ActivationLOG(),true,3));
      //network.addLayer(new BasicLayer(new ActivationReLU(),true,8));
     
      // Output layer
      //network.addLayer(new BasicLayer(new ActivationLOG(),false,1));
      network.addLayer(new BasicLayer(new ActivationTANH(),false,1));
      //network.addLayer(new BasicLayer(new ActivationReLU(),false,1));
      //network.addLayer(new BasicLayer(new ActivationSigmoid(),false,1));
    
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
           
         if (epoch >= 500 && network.calculateError(trainingSet) > 0.000000031)    // 0.000000091 
          { 
            returnCode = 1;
                              
            System.out.println("Try again");  
            return returnCode; 
          }  
       } while (network.calculateError(trainingSet) > 0.00000003);   // 0.00000009
         
      // Save the network file
      EncogDirectoryPersistence.saveObject(new File(networkFileName),network);
      
      System.out.println("Neural Network Results:");
      
      double sumNormDifferencePerc = 0.00;
      double averNormDifferencePerc = 0.00;
      double maxNormDifferencePerc = 0.00;
      
      int m = -1;                  // Record number in the input file
      double xPointer = -1.00; 
      
      for(MLDataPair pair: trainingSet)
        {
            m++;  
            xPointer = xPointer + 2.00; 
            
            //if(m == 0) 
            // continue;
                  
             final MLData output = network.compute(pair.getInput());
          
             MLData inputData = pair.getInput();
             MLData actualData = pair.getIdeal();
             MLData predictData = network.compute(inputData);
        
             // Calculate and print the results
             normInputXPointValue = inputData.getData(0); 
             normTargetXPointValue = actualData.getData(0);
             normPredictXPointValue = predictData.getData(0);
                       
             denormInputXPointValue = ((minXPointDl -
               maxXPointDh)*normInputXPointValue - Nh*minXPointDl +
                  maxXPointDh *Nl)/(Nl - Nh);
             
             denormTargetXPointValue = ((minTargetValueDl - maxTargetValueDh)*
               normTargetXPointValue - Nh*minTargetValueDl +
                 maxTargetValueDh*Nl)/(Nl - Nh);
             
             denormPredictXPointValue =((minTargetValueDl - maxTargetValueDh)*
               normPredictXPointValue - Nh*minTargetValueDl +
                 maxTargetValueDh*Nl)/(Nl - Nh);
            	        
             valueDifference = Math.abs(((denormTargetXPointValue -
               denormPredictXPointValue)/denormTargetXPointValue)*100.00); 
        
             System.out.println ("xPoint = " + denormTargetXPointValue +
                  "  denormPredictXPointValue = " + denormPredictXPointValue +
                    "  valueDifference = " + valueDifference);
                          
             sumNormDifferencePerc = sumNormDifferencePerc + valueDifference;
        
             if (valueDifference > maxNormDifferencePerc)
               maxNormDifferencePerc = valueDifference; 
     
             xData.add(denormInputXPointValue);
             yData1.add(denormTargetXPointValue);
             yData2.add(denormPredictXPointValue);
       
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
 
        averNormDifferencePerc  = sumNormDifferencePerc/intNumberOfRecordsInTrainFile;
        
        System.out.println(" ");
        System.out.println("maxErrorDifferencePerc = " + maxNormDifferencePerc + "  averErrorDifferencePerc = " + averNormDifferencePerc);
     
        returnCode = 0;
        return returnCode;
        
    }   // End of the method

   
   //==========================================================================
   // Load and test the trained network at the points not used in training.
   //==========================================================================
   static public void loadAndTestNetwork()
    { 
     System.out.println("Testing the networks results");
     
     List<Double> xData = new ArrayList<Double>();
     List<Double> yData1 = new ArrayList<Double>();   
     List<Double> yData2 = new ArrayList<Double>(); 
          
     double targetToPredictPercent = 0;
     double maxGlobalResultDiff = 0.00;
     double averGlobalResultDiff = 0.00;
     double sumGlobalResultDiff = 0.00; 
     double maxGlobalIndex = 0;
     double normInputXPointValueFromRecord = 0.00;
     double normTargetXPointValueFromRecord = 0.00;
     double normPredictXPointValueFromRecord = 0.00; 
     
     BufferedReader br4; 
     BasicNetwork network;
     
     int k1 = 0;
     int k3 = 0;
 
     maxGlobalResultDiff = 0.00;
     averGlobalResultDiff = 0.00;
     sumGlobalResultDiff = 0.00;  
        
     // Load the test dataset into mmemory
     MLDataSet testingSet =
     loadCSV2Memory(testFileName,numberOfInputNeurons,numberOfOutputNeurons,
       true,CSVFormat.ENGLISH,false);
       
     // Load the saved trained network
     network =
      (BasicNetwork)EncogDirectoryPersistence.loadObject(new File(networkFileName)); 
  
     int i = - 1; 
     double xPoint = -0.00;
    
     for (MLDataPair pair:  testingSet)
      {
           i++;
           xPoint = xPoint + 2.00;
        
           MLData inputData = pair.getInput();
           MLData actualData = pair.getIdeal();
           MLData predictData = network.compute(inputData);
        
           // These values are Normalized as the whole input is
           normInputXPointValueFromRecord = inputData.getData(0);
           normTargetXPointValueFromRecord = actualData.getData(0);
           normPredictXPointValueFromRecord = predictData.getData(0); 
               
           denormInputXPointValue = ((minXPointDl - maxXPointDh)*
             normInputXPointValueFromRecord - Nh*minXPointDl +
                maxXPointDh*Nl)/(Nl - Nh);
           
           denormTargetXPointValue = ((minTargetValueDl - maxTargetValueDh)*
             normTargetXPointValueFromRecord - Nh*minTargetValueDl +
                maxTargetValueDh*Nl)/(Nl - Nh);
           
           denormPredictXPointValue =((minTargetValueDl - maxTargetValueDh)*
             normPredictXPointValueFromRecord - Nh*minTargetValueDl +
                maxTargetValueDh*Nl)/(Nl - Nh);
              
           targetToPredictPercent = Math.abs((denormTargetXPointValue -
             denormPredictXPointValue)/denormTargetXPointValue*100);
                 
           System.out.println("xPoint = " + denormInputXPointValue +
             "  denormTargetXPointValue = " + denormTargetXPointValue +
               "  denormPredictXPointValue = " + denormPredictXPointValue +
                 "   targetToPredictPercent = " + targetToPredictPercent);
         
           if (targetToPredictPercent > maxGlobalResultDiff)
              maxGlobalResultDiff = targetToPredictPercent;
          
           sumGlobalResultDiff = sumGlobalResultDiff + targetToPredictPercent; 
                        
           // Populate chart elements
           xData.add(denormInputXPointValue);
           yData1.add(denormTargetXPointValue);
           yData2.add(denormPredictXPointValue);
          
       }  // End for pair loop
    
      // Print the max and average results
        
      System.out.println(" ");  
      averGlobalResultDiff = sumGlobalResultDiff/intNumberOfRecordsInTestFile;
                  
      System.out.println("maxErrorDifferencePercent = " + maxGlobalResultDiff);
      System.out.println("averErrorDifferencePercent = " + averGlobalResultDiff);
   
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
 
} // End of the class
