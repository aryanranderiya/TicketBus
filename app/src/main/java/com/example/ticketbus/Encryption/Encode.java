package com.example.ticketbus.Encryption;

public class Encode {
    public static String encode(String s) {
        // create a string to add in the initial
        // binary code for extra security
        String ini = "11111111";
        int cu = 0;

        // create an array
        int arr[] = new int[111111];

        // iterate through the string
        for (int i = 0; i < s.length(); i++) {
            // put the ascii value of
            // each character in the array
            arr[i] = (int) s.charAt(i);
            cu++;
        }
        String res = "";

        // create another array
        int bin[] = new int[111111];
        int idx = 0;

        // run a loop of the size of string
        for (int i1 = 0; i1 < cu; i1++) {

            // get the ascii value at position
            // i1 from the first array
            int temp = arr[i1];

            // run the second nested loop of same size
            // and set 0 value in the second array
            for (int j = 0; j < cu; j++) bin[j] = 0;
            idx = 0;

            // run a while for temp > 0
            while (temp > 0) {
                // store the temp module
                // of 2 in the 2nd array
                bin[idx++] = temp % 2;
                temp = temp / 2;
            }
            String dig = "";
            String temps;

            // run a loop of size 7
            for (int j = 0; j < 7; j++) {

                // convert the integer to string
                temps = Integer.toString(bin[j]);

                // add the string using
                // concatenation function
                dig = dig.concat(temps);
            }
            String revs = "";

            // reverse the string
            for (int j = dig.length() - 1; j >= 0; j--) {
                char ca = dig.charAt(j);
                revs = revs.concat(String.valueOf(ca));
            }
            res = res.concat(revs);
        }
        // add the extra string to the binary code
        res = ini.concat(res);

        // return the encrypted code
        return res;
    }
}