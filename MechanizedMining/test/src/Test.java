import java.util.Arrays;

public class Test {
  public static void main(String[] args) {
    for (int asterisk = '*'; asterisk < asterisk + 5; asterisk++) {
      for (int n = 0; n < 5; n++) {
        System.out.print(asterisk);
      }
      System.out.println();
    }
  }
}
