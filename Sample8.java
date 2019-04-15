// ===============================================================
// Approximation of the 3-D Function using conventional process.
// The input file is normalized.
// ===============================================================

package sample9;

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

public class Sample9 implements ExampleChart<XYChart>
{
    // Interval to normalize
   static double Nh =  1;  
   static double Nl = -1;  
  
   // First column
   static double minXPointDl = 2.00;
   static double maxXPointDh = 6.00; 
   
   // Second column
   static double minYPointDl = 2.00;
   static double maxYPointDh = 6.00; 
   
   // Third  column - target data
   static double minTargetValueDl = 45.00; 
   static double maxTargetValueDh = 55.00; 
   
   static double doublePointNumber = 0.00; 
   static int intPointNumber = 0;  
   static InputStream input = null;
   static double[] arrPrices = new double[2700]; 
   static double normInputXPointValue = 0.00; 
   static double normInputYPointValue = 0.00; 
   static double normPredictValue = 0.00;
   static double normTargetValue = 0.00;
   static double normDifferencePerc = 0.00;
   static double returnCode = 0.00;
   static double denormInputXPointValue = 0.00;
   static double denormInputYPointValue = 0.00;
   static double denormPredictValue = 0.00;
   static double denormTargetValue = 0.00;
   static double valueDifference = 0.00;
   static int numberOfInputNeurons;
   static int numberOfOutputNeurons;
    static int intNumberOfRecordsInTestFile;
   static String trainFileName;
   static String priceFileName;
   static String testFileName;
   static String chartTrainFileName;
   static String chartTrainFileNameY;
   static String chartTestFileName;
   static String networkFileName;
   static int workingMode;
   static String cvsSplitBy = ",";
   
   static int numberOfInputRecords = 0;
      
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
    //Chart = new  XYChartBuilder().width(900).height(500).title(getClass().
    //   getSimpleName()).xAxisTitle("y").yAxisTitle("z= f(y)").build();
    
    //Chart = new  XYChartBuilder().width(900).height(500).title(getClass().
    //          getSimpleName()).xAxisTitle("y").yAxisTitle("z= f(y)").build();
    
    // Customize Chart
    Chart.getStyler().setPlotBackgroundColor(ChartColor.getAWTColor(ChartColor.GREY));
    Chart.getStyler().setPlotGridLinesColor(new Color(255, 255, 255));
    
    //Chart.getStyler().setPlotBackgroundColor(ChartColor.getAWTColor(ChartColor.WHITE));
    //Chart.getStyler().setPlotGridLinesColor(new Color(0, 0, 0));
    Chart.getStyler().setChartBackgroundColor(Color.WHITE);
    //Chart.getStyler().setLegendBackgroundColor(Color.PINK);
    Chart.getStyler().setLegendBackgroundColor(Color.WHITE);
    //Chart.getStyler().setChartFontColor(Color.MAGENTA);
    Chart.getStyler().setChartFontColor(Color.BLACK);
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
    //Chart.getStyler().setLegendPosition(LegendPosition.InsideSE);
    Chart.getStyler().setLegendPosition(LegendPosition.OutsideS);
    Chart.getStyler().setLegendSeriesLineLength(12);
    Chart.getStyler().setAxisTitleFont(new Font(Font.SANS_SERIF, Font.ITALIC, 18));
    Chart.getStyler().setAxisTickLabelsFont(new Font(Font.SERIF, Font.PLAIN, 11));
    Chart.getStyler().setDatePattern("yyyy-MM");
    Chart.getStyler().setDecimalPattern("#0.00");
       
    try
       {
         // Configuration
         
         // Training mode 
         //workingMode = 1; 
         //numberOfInputRecords = 2602;
         //trainFileName = "C:/My_Neural_Network_Book/Book_Examples/Sample9_Calculate_Train_Norm.csv";   
         //chartTrainFileName = "C:/My_Neural_Network_Book/Book_Examples/Sample9_Chart_X_Training_Results.csv";   
         //chartTrainFileName = "C:/My_Neural_Network_Book/Book_Examples/Sample9_Chart_Y_Training_Results.csv";   
         
         // Testing mode
         workingMode = 2; 
         numberOfInputRecords = 2602;
         testFileName = "C:/My_Neural_Network_Book/Book_Examples/Sample9_Calculate_Test_Norm.csv";   
         chartTestFileName = "C:/My_Neural_Network_Book/Book_Examples/Sample9_Chart_X_Testing_Results.csv";   
         chartTestFileName = "C:/My_Neural_Network_Book/Book_Examples/Sample9_Chart_Y_Testing_Results.csv";
         
         // Common part of config data
         networkFileName = "C:/My_Neural_Network_Book/Book_Examples/Sample9_Saved_Network_File.csv";
         numberOfInputNeurons = 2;
         numberOfOutputNeurons = 1;
         
         // Check the working mode to run
         
         if(workingMode == 1)
          {   
            // Training mode
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
             }  while (returnCode > 0);
          } 
         else
          { 
             // Test mode  
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
      ExampleChart<XYChart> exampleChart = new Sample9();
      XYChart Chart = exampleChart.getChart();
      new SwingWrapper<XYChart>(Chart).displayChart();  
    } // End of the main method
   
   
   //==========================================================================
   // This method trains, Validates, and saves the trained network file
   //==========================================================================
   static public double trainValidateSaveNetwork()
    {
      // Load the training CSV file in memory
      MLDataSet trainingSet = 
        loadCSV2Memory(trainFileName,numberOfInputNeurons,numberOfOutputNeurons,
          true,CSVFormat.ENGLISH,false);  
                  
      // create a neural network
      BasicNetwork network = new BasicNetwork();
     
      // Input layer
      network.addLayer(new BasicLayer(null,true,numberOfInputNeurons));
      
      // Hidden layer
      network.addLayer(new BasicLayer(new ActivationTANH(),true,7));
      network.addLayer(new BasicLayer(new ActivationTANH(),true,7));
      network.addLayer(new BasicLayer(new ActivationTANH(),true,7));
      network.addLayer(new BasicLayer(new ActivationTANH(),true,7));
      network.addLayer(new BasicLayer(new ActivationTANH(),true,7));
      //network.addLayer(new BasicLayer(new ActivationTANH(),true,5));
      //network.addLayer(new BasicLayer(new ActivationTANH(),true,5));
      //network.addLayer(new BasicLayer(new ActivationTANH(),true,7));
      //network.addLayer(new BasicLayer(new ActivationTANH(),true,7));
      //network.addLayer(new BasicLayer(new ActivationTANH(),true,7));
      
      
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

      do
       {
	 train.iteration();
	 System.out.println("Epoch #" + epoch + " Error:" + train.getError());
	  
         epoch++;
         
         if (epoch >= 11000 && network.calculateError(trainingSet) > 0.00000091)    // 0.00000371
             { 
              returnCode = 1;
                              
              System.out.println("Try again");  
              return returnCode; 
             }    
       } while(train.getError() > 0.0000009);  // 0.0000037
          
      // Save the network file
      EncogDirectoryPersistence.saveObject(new File(networkFileName),network);
      
      System.out.println("Neural Network Results:");
      
      double sumNormDifferencePerc = 0.00;
      double averNormDifferencePerc = 0.00;
      double maxNormDifferencePerc = 0.00;
      
      int m = 0;                  // Record number in the input file
      double xPointer = 0.00; 
      
      for(MLDataPair pair: trainingSet)
        {
            m++;  
            xPointer++; 
            
            //if(m == 0) 
            // continue;
                  
             final MLData output = network.compute(pair.getInput());
          
             MLData inputData = pair.getInput();
             MLData actualData = pair.getIdeal();
             MLData predictData = network.compute(inputData);
        
             // Calculate and print the results
             normInputXPointValue = inputData.getData(0); 
             normInputYPointValue = inputData.getData(1); 
             normTargetValue = actualData.getData(0);
             normPredictValue = predictData.getData(0);
               
             denormInputXPointValue = ((minXPointDl - maxXPointDh)*normInputXPointValue -
               Nh*minXPointDl + maxXPointDh *Nl)/(Nl - Nh);
             
             denormInputYPointValue = ((minYPointDl - maxYPointDh)*normInputYPointValue -
               Nh*minYPointDl + maxYPointDh *Nl)/(Nl - Nh);
             
             denormTargetValue =((minTargetValueDl - maxTargetValueDh)* normTargetValue -
               Nh*minTargetValueDl + maxTargetValueDh*Nl)/(Nl - Nh);
             
             denormPredictValue =((minTargetValueDl - maxTargetValueDh)* normPredictValue -
               Nh*minTargetValueDl + maxTargetValueDh*Nl)/(Nl - Nh);
            
             
             valueDifference =
               Math.abs(((denormTargetValue - denormPredictValue)/denormTargetValue)*100.00); 
             
             
             System.out.println ("xPoint = " + denormInputXPointValue + "  yPoint = " + 
               denormInputYPointValue + "  denormTargetValue = " + 
                  denormTargetValue + "  denormPredictValue = " + denormPredictValue +
                    "  valueDifference = " + valueDifference);
             
             //System.out.println("intPointNumber = " + intPointNumber); 
             
             sumNormDifferencePerc = sumNormDifferencePerc + valueDifference;
        
             if (valueDifference > maxNormDifferencePerc)
               maxNormDifferencePerc = valueDifference; 
     
             xData.add(denormInputYPointValue);
             //xData.add(denormInputYPointValue);
             yData1.add(denormTargetValue);
             yData2.add(denormPredictValue);
       
        }   // End for pair loop

        XYSeries series1 = Chart.addSeries("Actual data", xData, yData1);
        XYSeries series2 = Chart.addSeries("Predict data", xData, yData2);
    
        series1.setLineColor(XChartSeriesColors.BLACK);
        series2.setLineColor(XChartSeriesColors.LIGHT_GREY);
      
        series1.setMarkerColor(Color.BLACK);
        series2.setMarkerColor(Color.WHITE);
        series1.setLineStyle(SeriesLines.SOLID);
        series2.setLineStyle(SeriesLines.SOLID);
        
        try
         {   
           //Save the chart image
           //BitmapEncoder.saveBitmapWithDPI(Chart, chartTrainFileName, 
           //  BitmapFormat.JPG, 100);
           
           BitmapEncoder.saveBitmapWithDPI(Chart,chartTrainFileName,BitmapFormat.JPG, 100);
           
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
 
        averNormDifferencePerc  = sumNormDifferencePerc/numberOfInputRecords;
        
        System.out.println(" ");
        System.out.println("maxErrorPerc = " + maxNormDifferencePerc + "  averErrorPerc = " + averNormDifferencePerc);
  
        returnCode = 0.00;
        return returnCode;

    }   // End of the method


   
   //=================================================
   // This method load and test the trainrd network
   //=================================================
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
     double normInputYPointValueFromRecord = 0.00;
     double normTargetValueFromRecord = 0.00;
     double normPredictValueFromRecord = 0.00; 
              
     BasicNetwork network;
             
     maxGlobalResultDiff = 0.00;
     averGlobalResultDiff = 0.00;
     sumGlobalResultDiff = 0.00;  
        
     // Load the test dataset into mmemory
     MLDataSet testingSet =
     loadCSV2Memory(testFileName,numberOfInputNeurons,numberOfOutputNeurons,true,
       CSVFormat.ENGLISH,false);
       
     // Load the saved trained network
     network =
       (BasicNetwork)EncogDirectoryPersistence.loadObject(new File(networkFileName)); 
  
     int i = - 1; // Index of the current record
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
           normInputYPointValueFromRecord = inputData.getData(1);
           normTargetValueFromRecord = actualData.getData(0);
           normPredictValueFromRecord = predictData.getData(0); 
               
           denormInputXPointValue = ((minXPointDl - maxXPointDh)*
             normInputXPointValueFromRecord - Nh*minXPointDl + maxXPointDh*Nl)/(Nl - Nh);
           
           denormInputYPointValue = ((minYPointDl - maxYPointDh)*
             normInputYPointValueFromRecord - Nh*minYPointDl + maxYPointDh*Nl)/(Nl - Nh);
           
           denormTargetValue = ((minTargetValueDl - maxTargetValueDh)*
             normTargetValueFromRecord - Nh*minTargetValueDl + maxTargetValueDh*Nl)/(Nl - Nh);
           
           denormPredictValue =((minTargetValueDl - maxTargetValueDh)*
             normPredictValueFromRecord - Nh*minTargetValueDl + maxTargetValueDh*Nl)/(Nl - Nh);
              
           targetToPredictPercent = Math.abs((denormTargetValue - denormPredictValue)/
             denormTargetValue*100);
                 
           System.out.println("xPoint = " + denormInputXPointValue + "  yPoint = " + denormInputYPointValue + "  TargetValue = " +
             denormTargetValue + "  PredictValue = " + denormPredictValue + "  DiffPerc = " + targetToPredictPercent);
         
           if (targetToPredictPercent > maxGlobalResultDiff)
              maxGlobalResultDiff = targetToPredictPercent;
          
           sumGlobalResultDiff = sumGlobalResultDiff + targetToPredictPercent; 
                        
           // Populate chart elements
           xData.add(denormInputXPointValue);
           yData1.add(denormTargetValue);
           yData2.add(denormPredictValue);
          
       }  // End for pair loop
    
      // Print the max and average results
      System.out.println(" ");  
      averGlobalResultDiff = sumGlobalResultDiff/numberOfInputRecords;
                  
      System.out.println("maxErrorPerc = " + maxGlobalResultDiff);
      System.out.println("averErrorPerc = " + averGlobalResultDiff);
   
      // All testing batch files have been processed 
      XYSeries series1 = Chart.addSeries("Actual data", xData, yData1);
      XYSeries series2 = Chart.addSeries("Predict data", xData, yData2);
    
      series1.setLineColor(XChartSeriesColors.BLACK);
      series2.setLineColor(XChartSeriesColors.LIGHT_GREY);
      
      series1.setMarkerColor(Color.BLACK);
      series2.setMarkerColor(Color.WHITE);
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
