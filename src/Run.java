//import fsm.FiniteStateMachine;
import robot.Robot;
import utils.Delay;

/**
 * Created by Theo Theodoridis.
 * Class    : Run
 * Version  : v1.0
 * Date     : © Copyright 28-07-2015
 * User     : ttheod
 * email    : t.theodoridis@salford.ac.uk
 * Comments : To run the Aria run interface for both, simulator and real robot.
 **/

public class Run
{
    private static Robot robot;
    private static ControlPanel panel;
    private static LabExercises exercise;

   /**
    * Method     : Run::Run()
    * Purpose    : Secondary Run class constructor.
    * Parameters : args : The program's arguments.
    * Returns    : Nothing.
    * Notes      : None.
    **/
    public Run(String args[])
    {
        robot = new Robot();
        robot.init(args, robot);
        exercise = new LabExercises(robot);
        panel = new ControlPanel(robot, 200);
       // panel.ShowGUI();
    }


   /**
    * Method     : Run::main()
    * Purpose    : Default main method which runs the Run class.
    * Parameters : - args : Initialization parameters.
    * Returns    : Nothing.
    * Notes      : None.
    **/
    public static void main(String args[])
    {
        boolean omFlag = false;
        boolean kitchenFlag = false;
        boolean shutdownFlag = false;

        new Run(args);
        //Delay.ms(15000); //delay added to select the colour for tracking before machine runs
        while(true)
        {
             System.out.printf
             (
                 "\rOdometry: X = %.1f, Y = %.1f, Th = %.1f, ",
                 robot.kinematics.getX(), robot.kinematics.getY(), robot.kinematics.getTh()
             );

            // Solution.
             if(!omFlag && !kitchenFlag)   //When navigation is not finished
             {
                 kitchenFlag = exercise.inKitchen(robot.kinematics.getX());
                 omFlag = exercise.navigate(100);  // run navigate and return true or false
             }
             else                                  // when flag is true(when navigation is complete), run track
             {
                 if(!exercise.avoid(100.0))       //when not avoiding
                 exercise.track(100.0);           //track
                 shutdownFlag = exercise.endZone(robot.kinematics.getX(),robot.kinematics.getY()); //shutdown flag is true when the end zone is reached

                 if(shutdownFlag)                //if shutdown flag is true
                 {
                 robot.shutDown();               //shutdown the robot
                 System.out.println("Tracking complete");
                 }
             }

            // [+]Mapping example:
            exercise.mapBuilder();            //Map the environment and robot trajectory

            Delay.ms(100);
        }
    }
}
