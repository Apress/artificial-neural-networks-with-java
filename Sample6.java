// ===========================================================================
// Example of using neural network for classification of objects.
// The normalized training/testing files consists of records of the following
// format: 3 input fields (word numbers)and 5 target fields (indicate the book
// the record belongs to).
// ===========================================================================

package sample6;

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

public class Sample6 implements ExampleChart<XYChart>
{
    // Interval to normalize data
   static double Nh;  
   static double Nl;  
   
   // Normalization parameters for workBook number
   static double minWordNumberDl;
   static double maxWordNumberDh; 
   
   // Normalization parameters for target values
   static double minTargetValueDl; 
   static double maxTargetValueDh; 
   
   static double doublePointNumber = 0.00; 
   static int intPointNumber = 0;  
   static InputStream input = null;
   static double[] arrPrices = new double[2500]; 
   static double normInputWordNumber_01 = 0.00; 
   static double normInputWordNumber_02 = 0.00; 
   static double normInputWordNumber_03 = 0.00; 
   static double denormInputWordNumber_01 = 0.00;
   static double denormInputWordNumber_02 = 0.00; 
   static double denormInputWordNumber_03 = 0.00; 
   static double normTargetBookNumber_01 = 0.00;
   static double normTargetBookNumber_02 = 0.00;
   static double normTargetBookNumber_03 = 0.00;
   static double normTargetBookNumber_04 = 0.00;
   static double normTargetBookNumber_05 = 0.00;
   static double normPredictBookNumber_01 = 0.00;
   static double normPredictBookNumber_02 = 0.00;
   static double normPredictBookNumber_03 = 0.00;
   static double normPredictBookNumber_04 = 0.00;
   static double normPredictBookNumber_05 = 0.00;
   static double denormTargetBookNumber_01 = 0.00;
   static double denormTargetBookNumber_02 = 0.00;
   static double denormTargetBookNumber_03 = 0.00;
   static double denormTargetBookNumber_04 = 0.00;
   static double denormTargetBookNumber_05 = 0.00;
   static double denormPredictBookNumber_01 = 0.00;
   static double denormPredictBookNumber_02 = 0.00;
   static double denormPredictBookNumber_03 = 0.00;
   static double denormPredictBookNumber_04 = 0.00;
   static double denormPredictBookNumber_05 = 0.00;
   static double normDifferencePerc = 0.00;
   static double denormPredictXPointValue_01 = 0.00;
   static double denormPredictXPointValue_02 = 0.00;
   static double denormPredictXPointValue_03 = 0.00;
   static double denormPredictXPointValue_04 = 0.00;
   static double denormPredictXPointValue_05 = 0.00;
   static double valueDifference = 0.00;
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
   static int returnCode;
   
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
  
    // Interval to normalize data   
    Nh =  1;  
    Nl = -1;  
   
   // Normalization parameters for workBook number
   double minWordNumberDl = 1.00;
   double maxWordNumberDh = 60.00; 
   
   // Normalization parameters for target values
   minTargetValueDl = 0.00; 
   maxTargetValueDh = 1.00;   
           
   // Configuration (comment and uncomment the appropriate configuration)
           
  
   // For training the network
   //workingMode = 1; 
   //intNumberOfRecordsInTestFile = 31;
   //trainFileName = "C:/My_Neural_Network_Book/Book_Examples/Sample6_Norm_Train_File.csv";   
    
   // For testing the trained network at non-trained points
   workingMode = 2; 
   intNumberOfRecordsInTestFile = 16;
   testFileName = "C:/My_Neural_Network_Book/Book_Examples/Sample6_Norm_Test_File.csv";    
             
   networkFileName = "C:/My_Neural_Network_Book/Book_Examples/Sample6_Saved_Network_File.csv";
   numberOfInputNeurons = 3;
   numberOfOutputNeurons = 5;
         
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
            
     returnCode = 0;    // Clear the return code variable
         
     do
      { 
        returnCode = trainValidateSaveNetwork();
      } while (returnCode > 0);
    }   // End the training mode         
   else
    { 
      // Test mode  
      loadAndTestNetwork();
    }
    
    Encog.getInstance().shutdown();
          
    return Chart;
    
   }  // End of the method 
      
  
   // =======================================================
   // Load CSV to memory.
   // @return The loaded dataset.
   // =======================================================
   public static MLDataSet loadCSV2Memory(String filename, int input, int ideal,
     boolean headers,CSVFormat format, boolean significance)
     {
        DataSetCODEC codec = new CSVDataCODEC(new File(filename), format,
          headers, input, ideal,significance);
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
      ExampleChart<XYChart> exampleChart = new Sample6();
      XYChart Chart = exampleChart.getChart();
      new SwingWrapper<XYChart>(Chart).displayChart();  
    } // End of the main method
   
   //==========================================================================
   // This method trains, validates, and saves the trained network file on disk
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
      network.addLayer(new BasicLayer(null,true,3));
      
      // Hidden layer
      network.addLayer(new BasicLayer(new ActivationTANH(),true,7));
      network.addLayer(new BasicLayer(new ActivationTANH(),true,7));
      network.addLayer(new BasicLayer(new ActivationTANH(),true,7));
      network.addLayer(new BasicLayer(new ActivationTANH(),true,7));
      network.addLayer(new BasicLayer(new ActivationTANH(),true,7));
      network.addLayer(new BasicLayer(new ActivationTANH(),true,7));
      
      //network.addLayer(new BasicLayer(new ActivationSigmoid(),true,3));
      //network.addLayer(new BasicLayer(new ActivationLOG(),true,3));
      //network.addLayer(new BasicLayer(new ActivationReLU(),true,8));
      
      
      // Output layer
      //network.addLayer(new BasicLayer(new ActivationLOG(),false,1));
      network.addLayer(new BasicLayer(new ActivationTANH(),false,5));
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
         
         if (epoch >= 1000 && network.calculateError(trainingSet) > 0.0000000000000012) // 0.0000041  0.000051
             { 
              returnCode = 1;
                              
              System.out.println("Try again");  
              return returnCode; 
             } 
         
           //} while(train.getError() > 0.02);
       } while (network.calculateError(trainingSet) > 0.0000000000000011); // 0.000004    0.00005
         
      // Save the network file
      EncogDirectoryPersistence.saveObject(new File(networkFileName),network);
      
      System.out.println("Neural Network Results:");
            
      int m = 0;                
            
      for(MLDataPair pair: trainingSet)
        {
             m++;  
     
             final MLData output = network.compute(pair.getInput());
          
             MLData inputData = pair.getInput();
             MLData actualData = pair.getIdeal();
             MLData predictData = network.compute(inputData);
        
             // Calculate and print the results
             
             normInputWordNumber_01 = inputData.getData(0); 
             normInputWordNumber_02 = inputData.getData(1); 
             normInputWordNumber_03 = inputData.getData(2); 
                                       
             normTargetBookNumber_01 = actualData.getData(0);
             normTargetBookNumber_02 = actualData.getData(1);
             normTargetBookNumber_03 = actualData.getData(2);
             normTargetBookNumber_04 = actualData.getData(3);
             normTargetBookNumber_05 = actualData.getData(4);
                          
             normPredictBookNumber_01 = predictData.getData(0);
             normPredictBookNumber_02 = predictData.getData(1);
             normPredictBookNumber_03 = predictData.getData(2);
             normPredictBookNumber_04 = predictData.getData(3);
             normPredictBookNumber_05 = predictData.getData(4);
             
             // De-normalize the results 
             denormInputWordNumber_01 = ((minWordNumberDl -
               maxWordNumberDh)*normInputWordNumber_01 - Nh*minWordNumberDl +
                  maxWordNumberDh *Nl)/(Nl - Nh);
             
             denormInputWordNumber_02 = ((minWordNumberDl -
               maxWordNumberDh)*normInputWordNumber_02 - Nh*minWordNumberDl +
                  maxWordNumberDh *Nl)/(Nl - Nh);
             
             denormInputWordNumber_03 = ((minWordNumberDl -
               maxWordNumberDh)*normInputWordNumber_03 - Nh*minWordNumberDl +
                  maxWordNumberDh *Nl)/(Nl - Nh);
                          
             denormTargetBookNumber_01 = ((minTargetValueDl - maxTargetValueDh)*
               normTargetBookNumber_01 - Nh*minTargetValueDl +
                 maxTargetValueDh*Nl)/(Nl - Nh);
             
             denormTargetBookNumber_02 = ((minTargetValueDl - maxTargetValueDh)*
               normTargetBookNumber_02 - Nh*minTargetValueDl +
                 maxTargetValueDh*Nl)/(Nl - Nh);
             
             denormTargetBookNumber_03 = ((minTargetValueDl - maxTargetValueDh)*
               normTargetBookNumber_03 - Nh*minTargetValueDl +
                 maxTargetValueDh*Nl)/(Nl - Nh);
             
             denormTargetBookNumber_04 = ((minTargetValueDl - maxTargetValueDh)*
               normTargetBookNumber_04 - Nh*minTargetValueDl +
                 maxTargetValueDh*Nl)/(Nl - Nh);
             
             denormTargetBookNumber_05 = ((minTargetValueDl - maxTargetValueDh)*
               normTargetBookNumber_05 - Nh*minTargetValueDl +
                 maxTargetValueDh*Nl)/(Nl - Nh);
             
             denormPredictBookNumber_01 =((minTargetValueDl - maxTargetValueDh)*
               normPredictBookNumber_01 - Nh*minTargetValueDl +
                 maxTargetValueDh*Nl)/(Nl - Nh);
            
             denormPredictBookNumber_02 =((minTargetValueDl - maxTargetValueDh)*
               normPredictBookNumber_02- Nh*minTargetValueDl +
                 maxTargetValueDh*Nl)/(Nl - Nh);
             
             denormPredictBookNumber_03 =((minTargetValueDl - maxTargetValueDh)*
               normPredictBookNumber_03 - Nh*minTargetValueDl +
                 maxTargetValueDh*Nl)/(Nl - Nh);
             
             denormPredictBookNumber_04 =((minTargetValueDl - maxTargetValueDh)*
               normPredictBookNumber_04 - Nh*minTargetValueDl +
                 maxTargetValueDh*Nl)/(Nl - Nh);
             
             denormPredictBookNumber_05 =((minTargetValueDl - maxTargetValueDh)*
               normPredictBookNumber_05 - Nh*minTargetValueDl +
                 maxTargetValueDh*Nl)/(Nl - Nh);
             
             System.out.println ("RecordNumber = " + m);
              
             System.out.println ("denormTargetBookNumber_01 = " + denormTargetBookNumber_01 +
                  "  denormPredictBookNumber_01 = " + denormPredictBookNumber_01);
             
             System.out.println ("denormTargetBookNumber_02 = " + denormTargetBookNumber_02 +
                  "  denormPredictBookNumber_02 = " + denormPredictBookNumber_02);
             
             System.out.println ("denormTargetBookNumber_03 = " + denormTargetBookNumber_03 +
                  "  denormPredictBookNumber_03 = " + denormPredictBookNumber_03);
             
             System.out.println ("denormTargetBookNumber_04 = " + denormTargetBookNumber_04 +
                  "  denormPredictBookNumber_04 = " + denormPredictBookNumber_04);
             
             System.out.println ("denormTargetBookNumber_05 = " + denormTargetBookNumber_05 +
                  "  denormPredictBookNumber_05 = " + denormPredictBookNumber_05);
             
            //System.out.println (" ");
                           
            // Print the classification results
            if(Math.abs(denormPredictBookNumber_01) > 0.85)
              if(Math.abs(denormPredictBookNumber_01) > 0.85  &
                 Math.abs(denormPredictBookNumber_02) < 0.2   &
                 Math.abs(denormPredictBookNumber_03) < 0.2   &
                 Math.abs(denormPredictBookNumber_04) < 0.2   &
                 Math.abs(denormPredictBookNumber_05) < 0.2)
                  {
                    System.out.println ("Record 1 belongs to book 1");
                    System.out.println (" ");
                  }
              else
               {
                 System.out.println ("Wrong results for record 1");
                 System.out.println (" ");
               } 
             
             if(Math.abs(denormPredictBookNumber_02) > 0.85)
              if(Math.abs(denormPredictBookNumber_01) < 0.2  &
                 Math.abs(denormPredictBookNumber_02) > 0.85 &
                 Math.abs(denormPredictBookNumber_03) < 0.2  &
                 Math.abs(denormPredictBookNumber_04) < 0.2  &
                 Math.abs(denormPredictBookNumber_05) < 0.2)
                  {
                    System.out.println ("Record 2 belongs to book 2");
                    System.out.println (" ");
                  }
               else
                {
                  System.out.println ("Wrong results for record 2");
                  System.out.println (" ");
                }
             
             if(Math.abs(denormPredictBookNumber_03) > 0.85)
              if(Math.abs(denormPredictBookNumber_01) < 0.2 &
                 Math.abs(denormPredictBookNumber_02) < 0.2  &
                 Math.abs(denormPredictBookNumber_03) > 0.85 &
                 Math.abs(denormPredictBookNumber_04) < 0.2  &
                 Math.abs(denormPredictBookNumber_05) < 0.2)
                  {
                    System.out.println ("Record 3 belongs to book 3");
                    System.out.println (" ");
                  }
              else
               {  
                 System.out.println ("Wrong results for record 3");
                 System.out.println (" ");
               }
             
             if(Math.abs(denormPredictBookNumber_04) > 0.85)
              if(Math.abs(denormPredictBookNumber_01) < 0.2  &
                 Math.abs(denormPredictBookNumber_02) < 0.2  &
                 Math.abs(denormPredictBookNumber_03) < 0.2  &
                 Math.abs(denormPredictBookNumber_04) > 0.85 &
                 Math.abs(denormPredictBookNumber_05) < 0.2)
                  {
                    System.out.println ("Record 4 belongs to book 4");
                    System.out.println (" ");
                  }
              else
               {  
                 System.out.println ("Wrong results for record 4");
                 System.out.println (" ");
               }
             
             if(Math.abs(denormPredictBookNumber_05) > 0.85)
              if(Math.abs(denormPredictBookNumber_01) < 0.2  &
                 Math.abs(denormPredictBookNumber_02) < 0.2  &
                 Math.abs(denormPredictBookNumber_03) < 0.2  &
                 Math.abs(denormPredictBookNumber_04) < 0.2 &
                 Math.abs(denormPredictBookNumber_05) > 0.85)
                  {
                    System.out.println ("Record 5 belongs to book 5");
                    System.out.println (" ");
                  }
              else
               {   
                 System.out.println ("Wrong results for record 5");
                 System.out.println (" ");
               }
            
        }   // End for pair loop

        returnCode = 0;
        return returnCode; 
        
    }   // End of the method
 
   //==========================================================================
   // Load and test the trained network at non-trainable points
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
     double normInputWordNumberFromRecord = 0.00;
     double normTargetBookNumberFromRecord = 0.00;
     double normPredictXPointValueFromRecord = 0.00; 
     BasicNetwork network;
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
  
     int i = 0; 
         
     for (MLDataPair pair:  testingSet)
      {
           i++;
                   
           MLData inputData = pair.getInput();
           MLData actualData = pair.getIdeal();
           MLData predictData = network.compute(inputData);
        
           // These values are Normalized as the whole input is
           normInputWordNumberFromRecord = inputData.getData(0);
           normTargetBookNumberFromRecord = actualData.getData(0);
           normPredictXPointValueFromRecord = predictData.getData(0); 
               
           denormInputWordNumber_01 = ((minWordNumberDl -
               maxWordNumberDh)*normInputWordNumber_01 - Nh*minWordNumberDl +
                  maxWordNumberDh *Nl)/(Nl - Nh);
             
             denormInputWordNumber_02 = ((minWordNumberDl -
               maxWordNumberDh)*normInputWordNumber_02 - Nh*minWordNumberDl +
                  maxWordNumberDh *Nl)/(Nl - Nh);
             
             denormInputWordNumber_03 = ((minWordNumberDl -
               maxWordNumberDh)*normInputWordNumber_03 - Nh*minWordNumberDl +
                  maxWordNumberDh *Nl)/(Nl - Nh);
             
           denormTargetBookNumber_01 = ((minTargetValueDl - maxTargetValueDh)*
               normTargetBookNumber_01 - Nh*minTargetValueDl +
                 maxTargetValueDh*Nl)/(Nl - Nh);
             
             denormTargetBookNumber_02 = ((minTargetValueDl - maxTargetValueDh)*
               normTargetBookNumber_02 - Nh*minTargetValueDl +
                 maxTargetValueDh*Nl)/(Nl - Nh);
             
             denormTargetBookNumber_03 = ((minTargetValueDl - maxTargetValueDh)*
               normTargetBookNumber_03 - Nh*minTargetValueDl +
                 maxTargetValueDh*Nl)/(Nl - Nh);
             
             denormTargetBookNumber_04 = ((minTargetValueDl - maxTargetValueDh)*
               normTargetBookNumber_04 - Nh*minTargetValueDl +
                 maxTargetValueDh*Nl)/(Nl - Nh);
             
             denormTargetBookNumber_05 = ((minTargetValueDl - maxTargetValueDh)*
               normTargetBookNumber_05 - Nh*minTargetValueDl +
                 maxTargetValueDh*Nl)/(Nl - Nh);
             
             denormPredictBookNumber_01 =((minTargetValueDl - maxTargetValueDh)*
               normPredictBookNumber_01 - Nh*minTargetValueDl +
                 maxTargetValueDh*Nl)/(Nl - Nh);
            
             denormPredictBookNumber_02 =((minTargetValueDl - maxTargetValueDh)*
               normPredictBookNumber_02- Nh*minTargetValueDl +
                 maxTargetValueDh*Nl)/(Nl - Nh);
             
             denormPredictBookNumber_03 =((minTargetValueDl - maxTargetValueDh)*
               normPredictBookNumber_03 - Nh*minTargetValueDl +
                 maxTargetValueDh*Nl)/(Nl - Nh);
             
             denormPredictBookNumber_04 =((minTargetValueDl - maxTargetValueDh)*
               normPredictBookNumber_04 - Nh*minTargetValueDl +
                 maxTargetValueDh*Nl)/(Nl - Nh);
             
             denormPredictBookNumber_05 =((minTargetValueDl - maxTargetValueDh)*
               normPredictBookNumber_05 - Nh*minTargetValueDl + 
                 maxTargetValueDh*Nl)/(Nl - Nh);
                 
             System.out.println ("RecordNumber = " + i);
              
             System.out.println ("denormTargetBookNumber_01 = " + denormTargetBookNumber_01 +
                  "  denormPredictBookNumber_01 = " + denormPredictBookNumber_01);
             
             System.out.println ("denormTargetBookNumber_02 = " + denormTargetBookNumber_02 +
                  "  denormPredictBookNumber_02 = " + denormPredictBookNumber_02);
             
             System.out.println ("denormTargetBookNumber_03 = " + denormTargetBookNumber_03 +
                  "  denormPredictBookNumber_03 = " + denormPredictBookNumber_03);
             
             System.out.println ("denormTargetBookNumber_04 = " + denormTargetBookNumber_04 +
                  "  denormPredictBookNumber_04 = " + denormPredictBookNumber_04);
             
             System.out.println ("denormTargetBookNumber_05 = " + denormTargetBookNumber_05 +
                  "  denormPredictBookNumber_05 = " + denormPredictBookNumber_05);
             
            //System.out.println (" ");
            
            if(Math.abs(denormPredictBookNumber_01) > 0.85  &
              Math.abs(denormPredictBookNumber_02) < 0.2   &
              Math.abs(denormPredictBookNumber_03) < 0.2   &
              Math.abs(denormPredictBookNumber_04) < 0.2   &
              Math.abs(denormPredictBookNumber_05) < 0.2 
              |
              Math.abs(denormPredictBookNumber_01) < 0.2  &
                 Math.abs(denormPredictBookNumber_02) > 0.85 &
                 Math.abs(denormPredictBookNumber_03) < 0.2  &
                 Math.abs(denormPredictBookNumber_04) < 0.2  &
                 Math.abs(denormPredictBookNumber_05) < 0.2
              
              | 
             Math.abs(denormPredictBookNumber_01) < 0.2  &
                 Math.abs(denormPredictBookNumber_02) > 0.85 &
                 Math.abs(denormPredictBookNumber_03) < 0.2  &
                 Math.abs(denormPredictBookNumber_04) < 0.2  &
                 Math.abs(denormPredictBookNumber_05) < 0.2 
              
              |
              Math.abs(denormPredictBookNumber_01) < 0.2 &
                 Math.abs(denormPredictBookNumber_02) < 0.2  &
                 Math.abs(denormPredictBookNumber_03) > 0.85 &
                 Math.abs(denormPredictBookNumber_04) < 0.2  &
                 Math.abs(denormPredictBookNumber_05) < 0.2
              
              
              |
             Math.abs(denormPredictBookNumber_01) < 0.2  &
                 Math.abs(denormPredictBookNumber_02) < 0.2  &
                 Math.abs(denormPredictBookNumber_03) < 0.2  &
                 Math.abs(denormPredictBookNumber_04) > 0.85 &
                 Math.abs(denormPredictBookNumber_05) < 0.2   
                
              |    
            Math.abs(denormPredictBookNumber_01) < 0.2  &
                 Math.abs(denormPredictBookNumber_02) < 0.2  &
                 Math.abs(denormPredictBookNumber_03) < 0.2  &
                 Math.abs(denormPredictBookNumber_04) < 0.2 &
                 Math.abs(denormPredictBookNumber_05) > 0.85)    
               
                {
                    System.out.println ("Record belong to some book");
                    System.out.println (" ");
                }     
            else
             {
                 System.out.println ("Unknown book");
                 System.out.println (" ");
             }  
       
       }  // End for pair loop
      
   } // End of the method
  
} // End of the class
