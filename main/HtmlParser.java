package hw3;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;

import java.text.DecimalFormat;

import java.util.Arrays;
import java.util.Comparator;

public class HtmlParser {
    public static void main(String[] args) {
        try {
            Document doc = Jsoup.connect("https://pd2-hw3.netdb.csie.ncku.edu.tw/").get();
            if(args[0].equals("0")){
                DataFile.dataRecord(doc);
            }
            else if(args[0].equals("1")){
                if(args[1].equals("0")){
                    OutputTask0.outputContent();
                }
                else if(args[1].equals("1")){
                    OutputTask1.outputContent(args[1], args[2], args[3], args[4]);
                }
                else if(args[1].equals("2")){
                    OutputTask2.outputContent(args[1], args[2], args[3], args[4]);
                }
                else if(args[1].equals("3")){
                    int argsCount = args.length;
                    OutputTask3.outputContent(args[1], args[args.length-2], args[args.length-1]);
                }
                else if(args[1].equals("4")){
                    int argsCount = args.length;
                    OutputTask4.outputContent(args[1], args[2], args[3], args[4]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class DataFile{
    public static void dataRecord(Document doc) throws IOException{//要記得throws IOException
        File file = new File("data.csv");
        FileWriter dataWriter;
        if (file.exists()) {
            dataWriter = new FileWriter(file, true);
        }else {
            dataWriter = new FileWriter(file);
            Elements tableRows = doc.select("tr");

        for (Element row : tableRows) {
            Elements cells = row.select("th");
            StringBuilder csvLine = new StringBuilder();
            for (Element cell : cells) {
                String stockName = cell.text();
                csvLine.append(stockName).append(",");
            }

            String line = csvLine.toString().replaceAll(",$", "");

            if (!line.trim().isEmpty()) {
                dataWriter.append(line).append("\n");
            }
        }

        dataWriter.flush();
        //dataWriter.close();
        }

        Elements tableRows = doc.select("tr");
        dataWriter.append(doc.title()+" ");
        for (Element row : tableRows) {
            Elements cells = row.select("td");

            StringBuilder csvLine = new StringBuilder();
            for (Element cell : cells) {
                String stockName = cell.text();
                csvLine.append(stockName).append(",");
            }

            String line = csvLine.toString().replaceAll(",$", "");

            if (!line.trim().isEmpty()) {
                dataWriter.append(line).append("\n");
            }
        }


        dataWriter.flush();
        dataWriter.close();
        System.out.println("CSV 檔案已生成。");
    }
}

class OutputTask0{
    public static void outputContent(){
        String[] dateOfStock = DateSort.arrangeDate();
        CsvGenerator.file(dateOfStock);

    }
}

class OutputTask1{
    public static void outputContent(String task, String stockName, String startDay, String endDay){
        String[] dateOfStock = DateSort.arrangeDate();
        String[] answer = new String[2];
        String[] stringArrayToString = Task1OrTask2.stockAverage(task, dateOfStock, stockName, startDay, endDay);
        answer[1] = stringArrayToString[0];
        answer[0] = stockName+","+startDay+","+endDay;
        CsvGenerator.file(answer);
    }
}

class OutputTask2{
    public static void outputContent(String task, String stockName, String startDay, String endDay){
        String[] dateOfStock = DateSort.arrangeDate();//取出指定天所有股票的值
        String[] averageOfStock = Task1OrTask2.stockAverage(task, dateOfStock, stockName, startDay, endDay);//取平均值
        String[] answer = new String[2];
        String[] stringArrayToString = StandardDeviation.caculate(dateOfStock, averageOfStock, startDay, endDay, stockName, task); //取標準差
        answer[1] = stringArrayToString[0];
        answer[0] = stockName+","+startDay+","+endDay;
        CsvGenerator.file(answer);
    }
}

class OutputTask3{
    public static void outputContent(String task, String startDay, String endDay){
        String[] dateOfStock = DateSort.arrangeDate();
        String[] answer = new String[2];
        String[] parts = dateOfStock[0].split(",");
        String[] StandardValueOfAllStock = new String[parts.length];
        for(int i = 0;i < parts.length; i++){
            String[] averageOfStock = Task1OrTask2.stockAverage(task, dateOfStock, parts[i], startDay, endDay);
            String[] stringArrayToString = StandardDeviation.caculate(dateOfStock, averageOfStock, startDay, endDay, parts[i], task);
            StandardValueOfAllStock[i] = stringArrayToString[0];
        }

        int[] TopThreeNumberOfStock = SortValue.sort(StandardValueOfAllStock);
        String[] TopThreeNameSave = new String[3];
        String[] TopThreeValueSave = new String[3];

        for(int i = 0; i < 3; i++ ){
            TopThreeNameSave[i] = parts[TopThreeNumberOfStock[i]];
        }
        for(int i = 0; i < 3; i++ ){
            TopThreeValueSave[i] = StandardValueOfAllStock[TopThreeNumberOfStock[i]];
        }

        StringBuilder sb1 = new StringBuilder();
        for (String value : TopThreeNameSave) {
            sb1.append(value).append(","); // 添加元素值和逗号
        }

        // 移除最后一个逗号
        if (sb1.length() > 0) {
            sb1.setLength(sb1.length() - 1);
        }

        StringBuilder sb2 = new StringBuilder();
        for (String value : TopThreeValueSave) {
            sb2.append(value).append(","); // 添加元素值和逗号
        }

        // 移除最后一个逗号
        if (sb2.length() > 0) {
            sb2.setLength(sb2.length() - 1);
        }
        answer[0] = sb1.toString()+","+startDay+","+endDay;
        answer[1] = sb2.toString();

        CsvGenerator.file(answer);
    }
}

class OutputTask4{
    public static void outputContent(String task, String stockName, String startDay, String endDay){
        String[] dateOfStock = DateSort.arrangeDate();//取出指定天所有股票的值
        String[] averageOfStock = Task1OrTask2.stockAverage(task, dateOfStock, stockName, startDay, endDay);//取平均值
        String[] answer = new String[2];
        String[] stringArrayToString = StandardDeviation.caculate(dateOfStock, averageOfStock, startDay, endDay, stockName, task); //取標準差
        answer[1] = stringArrayToString[0] + "," + stringArrayToString[1];
        answer[0] = stockName+","+startDay+","+endDay;
        CsvGenerator.file(answer);
    }
}

class DateSort{
    public static String[] arrangeDate(){
        BufferedReader reader = null;
        String[] dateOfStock = new String[40];
        Arrays.fill(dateOfStock, "");
        try {
            reader = new BufferedReader(new FileReader("data.csv"));
            String line;
            int rows = 0;

            while ((line = reader.readLine()) != null) {
                if(rows==0){
                    dateOfStock[0] = line;
                    rows++;
                    continue;
                }
                //rows++;
                if(rows!=0){
                    String modifiedString = line.replace("day", "");
                    String[] words = modifiedString.split("\\s+"); 
                    int day = Integer.parseInt(words[0]);
                    dateOfStock[day] = words[1];
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return dateOfStock;
    }
}

class CsvGenerator {
    public static void file(String[] dateOfStock) {
        FileWriter outputWriter = null;
        try {
            File file = new File("output.csv");
            if (!file.exists()) file.createNewFile();
            
            outputWriter=new FileWriter(file, true);

            for (String item : dateOfStock) {
                if (!item.equals("")) {
                    outputWriter.write(item);
                    outputWriter.write("\n");
                }
            }
            outputWriter.flush();
            System.out.println("Output file written successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (outputWriter != null) {
                    outputWriter.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}



class Task1OrTask2{
    public static String[] stockAverage(String task, String[] dateOfStock, String stockName, String startDay, String endDay){
        int start = Integer.parseInt(startDay);
        int end = Integer.parseInt(endDay); 
        int numberOfStocks = 0;
        int locationOfStock = 0;
        String[] designatedStockValue = new String[end-start+1];
        String[] parts = dateOfStock[0].split(",");
        
        for(int i=0 ; i<parts.length ; i++){
            if(parts[i].equals(stockName)){
                locationOfStock = i;
                break;
            }
        }

        for(int i=start ; i<=end ; i++){
            String[] parValue = dateOfStock[i].split(",");
            designatedStockValue[numberOfStocks] = parValue[locationOfStock];
            numberOfStocks++;
        }
        String[] joinedString = Average.value(task, start, end, designatedStockValue);
        return joinedString;
    }
}


class DemicalForm {
    public static double adjust(double ans) {
        // 格式化成字符串
        DecimalFormat df = new DecimalFormat("#.##");
        String formatted = df.format(ans);
        
        // 删除末尾的0
        if (formatted.endsWith(".0")) {
            formatted = formatted.substring(0, formatted.length() - 2);
        }
        ans = Double.parseDouble(formatted);
        return ans;
    }
}

class Average {
    public static String[] value(String task, int start, int end, String[] designatedStockValue) {
        String[] task1Answer = new String[40];
        double ans = 0;
        int element = 0;
        if (task.equals("1")) {
            for (int i = 0; i <= end - start - 4; i++) {
                double sum = 0;
                for (int j = i; j <= i + 4; j++) {
                    double value = Double.parseDouble(designatedStockValue[j]);
                    sum += value;
                }
                ans = sum / 5;
                ans = DemicalForm.adjust(ans);
                int roundedInt = (int) (ans * 100 + 0.5);
                ans = roundedInt / 100.0;

                String strNumber = String.valueOf(ans);
                task1Answer[i] = strNumber;
                element++;
            }
        } else if (task.equals("2")||task.equals("3")||task.equals("4")) {
            double sum = 0;
            double value = 0;
            for (int i = 0; i < end-start+1; i++) {
                value = Double.parseDouble(designatedStockValue[i]);
                sum += value;
            }
            ans = sum / (end - start + 1);
            String strNumber = String.valueOf(ans);
            task1Answer[0] = strNumber;
            element++;
        }

        String[] joinedString = new String[1];

        for (int i = element; i < task1Answer.length; i++) {
            task1Answer[i] = "";
        }

        String[] nonNullArray = new String[element];
        int index = 0;
        for (String str : task1Answer) {
            if (!str.isEmpty()) {
                nonNullArray[index++] = str;
            }
        }

        joinedString[0] = String.join(",", nonNullArray);
        return joinedString;
    }
}


class StandardDeviation{
    public static String[] caculate(String[] dateOfStock, String[] averageOfStock, String startDay, String endDay, String stockName, String task){
        int start = Integer.parseInt(startDay);
        int end = Integer.parseInt(endDay); 
        int numberOfStocks = 0;
        int locationOfStock = 0;
        String[] designatedStockValue = new String[end-start+1];
        String[] parts = dateOfStock[0].split(",");
        
        for(int i=0 ; i<parts.length ; i++){
            if(parts[i].equals(stockName)){
                locationOfStock = i;
                break;
            }
        }

        for(int i=start ; i<=end ; i++){
            String[] parValue = dateOfStock[i].split(",");
            designatedStockValue[numberOfStocks] = parValue[locationOfStock];
            numberOfStocks++;
        }
        
        double averageValue = Double.parseDouble(averageOfStock[0]);
        double[] designatedStockValueInDoubleType = new double[designatedStockValue.length];

        for (int i = 0; i < designatedStockValue.length; i++) {
            designatedStockValueInDoubleType[i] = Double.parseDouble(designatedStockValue[i]);
        }

        if(task.equals("2")||task.equals("3")){
            double sumOfSquareValue = 0;
            for (int i = 0;i < designatedStockValue.length; i++){
                double record = (averageValue - designatedStockValueInDoubleType[i]) * (averageValue - designatedStockValueInDoubleType[i]);
                sumOfSquareValue += record;
            }

            double valueOfStandardDeviation = sumOfSquareValue / (designatedStockValue.length-1);
            valueOfStandardDeviation = SquareRootCalculator.calculateSquareRoot(valueOfStandardDeviation);
            valueOfStandardDeviation = DemicalForm.adjust(valueOfStandardDeviation);
            String[] answer = new String[1];
            answer[0] = String.valueOf(valueOfStandardDeviation);
            if(answer[0].endsWith(".0")) answer[0] = answer[0].replace(".0", "");
            return answer;
        }
        else if(task.equals("4")){
            double averageOfTime = 0;
            double sumOfTime = 0;
            double timeDurable = (end - start + 1);
            for(int i = start; i <= end; i++){
                sumOfTime += i;
            }

            averageOfTime = sumOfTime/timeDurable;
            double numeratorOfB1 = 0;
            double denominatorOfB1 = 0;
            double Time = start;
            for (int i = 0;i < designatedStockValue.length; i++){
                double record = (designatedStockValueInDoubleType[i] - averageValue);
                double recordTime = Time - averageOfTime;

                numeratorOfB1 += (recordTime * record);
                Time += 1;
                denominatorOfB1 += recordTime * recordTime;
            }

            double b1 = numeratorOfB1 / denominatorOfB1;
            double b0 = averageValue - b1 * averageOfTime;

            b1 = DemicalForm.adjust(b1);
            b0 = DemicalForm.adjust(b0);
            String[] answer = new String[2];
            answer[0] = String.valueOf(b1);
            answer[1] = String.valueOf(b0);
            if(answer[0].endsWith(".0")) answer[0] = answer[0].replace(".0", "");
            if(answer[1].endsWith(".0")) answer[1] = answer[1].replace(".0", "");
            return answer;
        }
        return new String[0];
    }
}

class SquareRootCalculator {
    public static double calculateSquareRoot(double num) {
        // 初始的近似值
        double x0 = num / 2.0;
        // 迭代計算，直到達到足夠的精度
        while (true) {
            // 牛頓法公式：x1 = (x0 + num / x0) / 2
            double x1 = (x0 + num / x0) / 2.0;
            double sum = x1 - x0;
            // 檢查是否達到所需的精度，這裡我們使用差值小於一個極小值 0.00001 來表示
            if(sum<0){
                sum = 0 - sum;
            }
            if (sum < 0.000000000000001) {
                
                return x1;
            }
            
            // 更新近似值，準備下一輪迭代
            x0 = x1;
        }
    }
}

class SortValue{
    public static int[] sort(String[] StandardValueOfAllStock){
        double[] doubleArray = new double[StandardValueOfAllStock.length];

        // 遍历字符串数组，并将每个字符串转换为 double 类型
        for (int i = 0; i < StandardValueOfAllStock.length; i++) {
            doubleArray[i] = Double.parseDouble(StandardValueOfAllStock[i]);
        }

        // 将 double 数组转换为 Double 对象数组
        Double[] doubleObjArray = new Double[doubleArray.length];
        for (int i = 0; i < doubleArray.length; i++) {
            doubleObjArray[i] = doubleArray[i];
        }

        // 使用自定义的逆向比较器进行排序
        Arrays.sort(doubleObjArray, new Comparator<Double>() {
            @Override
            public int compare(Double a, Double b) {
                // 将比较结果反转以实现从大到小排序
                return Double.compare(b, a);
            }
        });

        int[] TopThreeNumberOfStock = new int[3];
        for(int i = 0;i < doubleArray.length; i++){
            if(doubleObjArray[0] == doubleArray[i]){
                TopThreeNumberOfStock[0] = i;
            }
            else if(doubleObjArray[1] == doubleArray[i]){
                TopThreeNumberOfStock[1] = i;
            }
            else if(doubleObjArray[2] == doubleArray[i]){
                TopThreeNumberOfStock[2] = i;
            }
        }
        return TopThreeNumberOfStock;
    }
}