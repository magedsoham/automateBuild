import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;

public class Utils {
    private static String s;
    public static ArrayList<String> getListFromFile(File path) throws FileNotFoundException {
        Scanner s = new Scanner(path);
        ArrayList<String> list = new ArrayList<String>();
        while (s.hasNextLine()){
            list.add(s.nextLine());
        }
        s.close();
        return list;
    }
    public static void setListToFile(ArrayList<String>arrayList,String path) throws IOException {
        FileWriter writer = new FileWriter(path);
        for(String str: arrayList) {
            writer.write(str);
        }
        writer.close();
    }
    public static ArrayList<String> fillArrayListFromCommandLine(Process process) throws IOException {
        ArrayList<String> list = new ArrayList<String>();
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
        while ((s = stdInput.readLine()) != null) {
            list.add(s);
        }
        return list;
    }
    public static String openChooserAndGetPath(JFileChooser chooser, String choosertitle) {
        chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File("."));
        chooser.setDialogTitle(choosertitle);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
        if (chooser.showOpenDialog(chooser.getParent()) == JFileChooser.APPROVE_OPTION) {
            System.out.println("getCurrentDirectory(): "
                    + chooser.getCurrentDirectory());
            System.out.println("getSelectedFile() : "
                    + chooser.getSelectedFile());
        } else {
            System.out.println("No Selection ");
        }
        return chooser.getSelectedFile() != null ? chooser.getSelectedFile().toString() : null;
    }

    public static Process runCommand(String command, File path) throws IOException, InterruptedException {
        Process p= Runtime.getRuntime().exec(command, null, path);
        return p;
    }

    private static ArrayList<String> showErrorInLog(Process p) throws IOException {
        s = null;
        ArrayList<String>tags = new ArrayList<String>();
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
        BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
        // read the output from the command
        System.out.println("Here is the standard output of the command:\n");
        while ((s = stdInput.readLine()) != null) {
            System.out.println(s);
            tags.add(s);
        }
        // read any errors from the attempted command
        System.out.println("Here is the standard error of the command (if any):\n");
        while ((s = stdError.readLine()) != null) {
            System.out.println(s);
        }
        stdInput.close();
        stdError.close();
        return tags;

    }

    public static void checkoutTag(String tag, String repoPath) throws IOException, InterruptedException {
        runCommand("git checkout " + tag, new File(repoPath));
    }
    public static Collection<?> getTags() throws IOException, InterruptedException {
        ArrayList <String> tags =new ArrayList<String>(showErrorInLog(runCommand("git tag", new File(MainPage.REPO_FILE))));
        return tags;
    }
    public static Collection<?> getBuildTypes() {
        ArrayList <String> buildTypes =new ArrayList<String>();
        buildTypes.add("release");
        buildTypes.add("debug");
        return buildTypes;
    }
    public static Collection<?> getCertificateStatus() {
        ArrayList <String> status =new ArrayList<String>();
        status.add("false");
        status.add("true");
        return status;
    }

    public static void getAPK(String tag, String url, String enableCP, String CID, String repoPath) throws IOException, InterruptedException {
        StringBuilder c= new StringBuilder("cmd /c gradlew.bat " + tag + " task runProgram"
                + " -P url=\"\\\"" + url+"\\\"\""
                + " -P EnableCertificatePinning=" + enableCP
                + " -P CertificateNewId=" + CID);
        showErrorInLog(runCommand(c.toString(), new File(repoPath)));
    }

    public static void uploadToStore(String currentPath, String currentFileName, String newPath, String newFileName) throws IOException, InterruptedException {
      showErrorInLog(runCommand("cmd /c copy " + "\"" + currentPath + "\\" + currentFileName + "\"" + " "
                        + "\"" + newPath + "\\" + newFileName + "\""
                , null));
    }

}
