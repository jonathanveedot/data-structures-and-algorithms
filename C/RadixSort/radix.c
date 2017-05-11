/*
 * radix.c
 * Jonathan Velez
 * =======
 * This programming recitation demonstrates the design of reusable data 
 * structures (queue and node) to perform a most significant digit radix sort 
 * on an array of integers. The avg runtime performance of this sort is O(nk),
 * where n is the number of keys of size k.
 *
 * ACCOMPANYING FILES:
 * queue.c, queue.h, node.c, node.h
 * Note: queue.c and node.c do not implement clean-up functions. These functions 
 * would be necessary for larger scale applications
 */

#include <stdio.h>
#include <stdlib.h>
#include "queue.h"

#define LENGTH 7

void RadixSort(int *array, int n)
{
	// This assumes n > 0, and array != NULL.
	int i, j, pow, max = array[0];
	
	Queue *buckets[10];
	
	// create some buckets
	for (i = 0; i < 10; i++)
		buckets[i] = initQueue();
	
	// find the max element in the array
	for (i = 1; i < n; i++)
		if (array[i] > max)
			max = array[i];
	
	// Recall that if we want to pull out the ones, tens, or hundreds digits of
	// some number, we can use:
	//
	// 402 % 10 = 2         <- ones digit
	// 402 / 1 % 10 = 2     <- also for the ones digit
	// 402 / 10 % 10 = 0    <- tens digit
	// 402 / 100 % 10 = 4   <- hundreds digit
	
	// loop for each digit in the max integer
	for (pow = 1; max / pow > 0; pow *= 10)
	{
		// place into buckets
		for (i = 0; i < n; i++)
			enqueue(buckets[ array[i] / pow % 10 ], array[i]);
		
		// pull out of buckets and into the original array
		for (i = j = 0; i < 10; i++)
			while (!isEmpty(buckets[i]))
				array[j++] = dequeue(buckets[i]);
	}
	
	// Clean up after yourself. We don't need to call a destroyQueue() function,
	// because we know each queue is empty by the time we get here.
	for (i = 0; i < 10; i++)
		free(buckets[i]);
}

// Print an array of n integers, space-separated, followed by two newline chars.
void printArray(int *array, int n)
{
	int i;

	for (i = 0; i < n; i++)
		printf("%d%s", array[i], (i == n - 1) ? "\n" : ", ");

	printf("\n");
}

int main(void)
{
	// Note: Since LENGTH is #defined, we don't need to do this dynamically...
	int i, *array = malloc(sizeof(int) * LENGTH);

	// Seed the random number generator.
	srand(time(NULL));

	// Populate our array with random numbers on the range [0, 99].
	for (i = 0; i < LENGTH; i++)
		array[i] = rand() % 100;

	// Print the unsorted array.
	printf("Unsorted array:\n");
	printArray(array, LENGTH);

	// Sort the array.
	RadixSort(array, LENGTH);

	// Print the sorted array.
	printf("Sorted array:\n");
	printArray(array, LENGTH);

	return 0;
}
