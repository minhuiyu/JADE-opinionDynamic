package OpinionDynamics;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import OpinionDynamics.dbhelper.Opinion;

public class LinearRegression {
	private double [][] trainData;//训练数据，一行一个数据，每一行最后一个数据为 y
    private int row;//训练数据  行数
    private int column;//训练数据 列数
    
    private double [] theta;//参数theta
    
    public double[] getTheta() {
		return theta;
	}

	private double alpha;//训练步长
    
    private int iteration;//迭代次数
    
//    public LinearRegression(String fileName)
//    {   
//        int rowoffile=getRowNumber(fileName);//获取输入训练数据文本的   行数
//        
//        int columnoffile = getColumnNumber(fileName);//获取输入训练数据文本的   列数
//        
//        trainData = new double[rowoffile][columnoffile+1];//这里需要注意，为什么要+1，因为为了使得公式整齐，我们加了一个特征x0，x0恒等于1
//        
//        this.row=rowoffile;
//        
//        this.column=columnoffile+1;
//        
//        this.alpha = 0.001;//步长默认为0.001
//        
//        this.iteration=100000;//迭代次数默认为 100000
//        
//        theta = new double [column-1];//h(x)=theta0 * x0 + theta1* x1 + theta2 * x2 + .......
//        
//        initialize_theta();
//        
//        loadTrainDataFromFile(fileName,rowoffile,columnoffile);
//    }
    
    public LinearRegression(List<Opinion> userOpList, List<Opinion> modeOpList, List<Opinion> friendOpList ,List<Opinion> resultOp ,double alpha,int iteration)
    {   
        int rowoffile=userOpList.size();//获取输入训练数据文本的   行数 
        System.out.println("LR"+userOpList.size());
        System.out.println(friendOpList.size());
        System.out.println(modeOpList.size());
        int columnoffile = 4;//获取输入训练数据文本的   列数
//        System.out.println(columnoffile);
        trainData = new double[rowoffile][columnoffile+1];//这里需要注意，为什么要+1，因为为了使得公式整齐，我们加了一个特征x0，x0恒等于1
        this.row=rowoffile;
        this.column=columnoffile+1;
        
        this.alpha = alpha;
        this.iteration=iteration;
        
        theta = new double [column-1];//h(x)=theta0 * x0 + theta1* x1 + theta2 * x2 + .......
        initialize_theta();
        for(int i = 0 ; i < row; i++)
        {
        	trainData[i][0] = 1;
        	trainData[i][1] = userOpList.get(i).getRealOpin();
        	trainData[i][2] = friendOpList.get(i).getRealOpin();
        	trainData[i][3] = modeOpList.get(i).getRealOpin();
        	trainData[i][4] = resultOp.get(i).getRealOpin(); 
        }
//       loadTrainDataFromFile(fileName,rowoffile,columnoffile);
    }
    
    
//    private int getRowNumber(String fileName)
//    {
//        int count =0;
//        File file = new File(fileName);
//        BufferedReader reader = null;
//        try {
//            reader = new BufferedReader(new FileReader(file));
//            while ( reader.readLine() != null) 
//                count++;
//            reader.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (reader != null) {
//                try {
//                    reader.close();
//                } catch (IOException e1) {
//                }
//            }
//        }
//        return count;
//        
//    }
    
//    private int getColumnNumber(String fileName)
//    {
//        int count =0;
//        File file = new File(fileName);
//        BufferedReader reader = null;
//        try {
//            reader = new BufferedReader(new FileReader(file));
//            String tempString = reader.readLine();
//            if(tempString!=null)
//                count = tempString.split(" ").length;
//            reader.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (reader != null) {
//                try {
//                    reader.close();
//                } catch (IOException e1) {
//                }
//            }
//        }
//        return count;
//    }
    
    private void initialize_theta()//将theta各个参数全部初始化为1.0
    {
        for(int i=0;i<theta.length;i++)
            theta[i]=1.0;
    }
    
    public void trainTheta()
    {
        int iteration = this.iteration;
        while( (iteration--)>0 )
        {
        	double sum = 0.0;
                //对每个theta i 求 偏导数
            double [] partial_derivative = compute_partial_derivative();//偏导数
                //更新每个theta
            for(int i =0; i< theta.length-1;i++)
            {
                theta[i]-= alpha * partial_derivative[i];
            	sum += theta[i];
            }
            theta[theta.length-1] = 1-sum;
        }
    }
    
    private double [] compute_partial_derivative()
    {
        double [] partial_derivative = new double[theta.length];
        for(int j =0;j<theta.length;j++)//遍历，对每个theta求偏导数
        {
            partial_derivative[j]= compute_partial_derivative_for_theta(j);//对 theta i 求 偏导
        }
        return partial_derivative;
    }
    private double compute_partial_derivative_for_theta(int j)
    {
        double sum=0.0;
        for(int i=0;i<row;i++)//遍历 每一行数据
        {
            sum+=h_theta_x_i_minus_y_i_times_x_j_i(i,j);
        }
        return sum/row;
    }
    private double h_theta_x_i_minus_y_i_times_x_j_i(int i,int j)
    {
        double[] oneRow = getRow(i);//取一行数据，前面是feature，最后一个是y
        double result = 0.0;
        
        for(int k=0;k< (oneRow.length-1);k++)
            result+=theta[k]*oneRow[k];
        result-=oneRow[oneRow.length-1];
        result*=oneRow[j];
        return result;
    }
    private double [] getRow(int i)//从训练数据中取出第i行，i=0，1，2，。。。，（row-1）
    {
        return trainData[i];
    }
    
    
//    private void loadTrainDataFromFile(String fileName,int row, int column)
//    {   
//        for(int i=0;i< row;i++)//trainData的第一列全部置为1.0（feature x0）
//            trainData[i][0]=1.0;
//        
//        File file = new File(fileName);
//        BufferedReader reader = null;
//        try {
//            reader = new BufferedReader(new FileReader(file));
//            String tempString = null;
//            int counter = 0;
//            while ( (counter<row) && (tempString = reader.readLine()) != null) {
//                String [] tempData = tempString.split(" ");
//                for(int i=0;i<column;i++)
//                    trainData[counter][i+1]=Double.parseDouble(tempData[i]);
//                counter++;
//            }
//            reader.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (reader != null) {
//                try {
//                    reader.close();
//                } catch (IOException e1) {
//                }
//            }
//        }
//    }
    
    public void printTrainData()
    {
        System.out.println("Train Data:\n");
        for(int i=0;i<column-1;i++)
            System.out.printf("%10s","x"+i+" ");
        System.out.printf("%10s","y"+" \n");
        for(int i=0;i<row;i++)
        {
            for(int j=0;j<column;j++)
            {
                System.out.printf("%10s",trainData[i][j]+" ");
            }
            System.out.println();
        }
        System.out.println();
    }
    
    public void printTheta()
    {
        for(double a:theta)
            System.out.print(a+" ");
    }

//	public static void main(String[] args) {
//		// TODO Auto-generated method stub
//		String fileName="D://weka//Weka-3-6//data//test.txt";
//		LinearRegression m = new LinearRegression(fileName,0.001,1000000);
//        m.printTrainData();
//        m.trainTheta();
//        m.printTheta();
//	}

}
