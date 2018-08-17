//package com.example.sidkathuria14.myapplication;
//
///**
// *
// * @author Pandurang Kamath
// * Encrypts the input matrix by XORing based on the keys kR and kC
// *
// */
//public class Encrypt {
//
//	public int[][] encrypt(int[][] matrix, int[] kR, int[] kC) {
//		int[] rotKR = Utils.rotate(kR);
//		int[] rotKC = Utils.rotate(kC);
//
//		int M = matrix.length;
//		int N = matrix[0].length;
//
//		for (int i = 0; i < M; i += 2) {
//			for (int j = 0; j < N; j++) {
//				matrix[i][j] = matrix[i][j] ^ rotKC[j];
//				if (i + 1 < M) {
//					matrix[i + 1][j] = matrix[i + 1][j] ^ kC[j];
//				}
//			}
//		}
//
//		for (int i = 0; i < M; i++) {
//			for (int j = 0; j < N; j += 2) {
//				matrix[i][j] = matrix[i][j] ^ rotKR[i];
//				if (j + 1 < N) {
//					matrix[i][j + 1] = matrix[i][j + 1] ^ kR[i];
//				}
//			}
//		}
//
//		return matrix;
//	}
//
//}
