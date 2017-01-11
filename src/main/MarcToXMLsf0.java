package main;

import java.io.IOException;

public class MarcToXMLsf0 {

  public static void main (String args[]) throws IOException {

    System.err.printf("\nCONVERTING MARC TO XML\n%n");

    Transfom.record(args[0]);

    System.err.println("DONE WITH MARCXML CONVERSION\n");
  }
}
