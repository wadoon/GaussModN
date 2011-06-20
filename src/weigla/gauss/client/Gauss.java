package weigla.gauss.client;

import static weigla.gauss.client.Util.*;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.core.client.GWT;

public class Gauss {
    static List<String> ops = new LinkedList<String>();

    public static int[][] gauss(int[][] M, int m, int n, int modul) {
	ops.clear();
	return rueckwaertsSubstitution(dreiecksElimination(M, m, n, modul), m,
		n, modul);
    }

    public static int[][] rueckwaertsSubstitution(int[][] M, int m, int n,
	    int modul) {
	// 1 2 3 4 5
	// 0 1 2 3 4
	// 0 0 1 2 3
	// 0 0 0 1 0 a b c
	// 0 0 0 0 1 x y z

	for (int row = n - 2; row >= 0; row--) {
	    for (int column = row + 1; column < n; column++) {
		int factor = M[row][column];
		log("[%d] = [%d] - (%d) [%d]", row, row, M[row][column], column);
		for (int c = column; c < m; c++) {
		    M[row][c] = mod(M[row][c] - factor * M[column][c], modul);
		}
		log(M);
	    }
	}
	return M;
    }

    public static int[][] dreiecksElimination(int[][] M, int m, int n, int modul) {
	for (int j = 0; j < n; j++) {
	    final int gcdAlpha = (int) alpha(M[j][j], modul);
	    for (int i = j + 1; i < n; i++) {
		// Hier anders als Gauss
		int faktor = mod((int) (M[i][j] * gcdAlpha), modul);

		log("[%] = [%] - (%)(%) [%]", i, i, M[i][j], gcdAlpha, j);

		for (int k = j; k < M[j].length; k++) {
		    M[i][k] = mod(M[i][k] - faktor * M[j][k], modul);
		}
		log(M);
	    }

	    if (M[j][j] != 1) {
		log("[%] = (%) [%]", j, gcdAlpha, j);
		// pivot element auf 1
		for (int c = 0; c < m; c++) {
		    M[j][c] = mod(M[j][c] * gcdAlpha, modul);
		}
		log(M);
	    }
	}
	return M;
    }

    private static void log(int[][] M)
    {
	String string = "<table>";
	for (int i = 0; i < M.length; i++) {
	    string += "<tr>";
	    for (int j = 0; j < M[i].length; j++) {
		string += "<td>" + M[i][j] + "</td>";
	    }
	    string += "</tr>";
	}
	string += "</table>";
	ops.add(string);
	GWT.log(string);
    }
    
    private static void log(String string,  Integer... a) {
	for (Integer integer : a) {
	    string = string.replaceFirst("%", "" + integer);
	}

	GWT.log(string);
	ops.add(string);
    }
}