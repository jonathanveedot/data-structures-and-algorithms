// MergeSort.c
// Jonathan Velez
// ================
// This is the MergeSort function along with its runtime analysis

// T(1) = c
// T(n) = T(n/2) + T(n/2) + n
//      = 2 T(n/2) + n
//      = 2 [2 T(n/4) + (n/2)] + n
//      = 4 T(n/4) + 2n
//      = 4 [2 T(n/8) + (n/4)] + 2n
//      = 8 T(n/8) + 3n
//      = ...
//      = 2^k T(n/(2^k)) + k n
//
// Suppose (n/(2^k)) = 1. Then, n = 2^k => k = lg n
//
//      = 2^(log_2 n) * T(1) + k n
//      = 2^(log_2 n) * c + k n
//      = 2^(log_2 n) * c + k n
//      = n * c + (log_2 n) * n
//
//      => O(n log n)


#include <stdio.h>
#include <stdlib.h>

#define LENGTH 7

void MergeSort(int *array, int lo, int hi)
{	
	int mid = lo + (hi - lo) / 2, i = lo, j = mid + 1, k = 0;
	
	int *aux = NULL;
	
	// base case: one (or fewer) elements in this portion of the array
	if (lo >= hi)
		return;
	
	// recursive calls
	MergeSort(array, lo, mid);
	MergeSort(array, mid+1, hi);
	
	// mergey merge
	aux = malloc(sizeof(int) * (hi - lo + 1));
	
	while (i <= mid || j <= hi)
	{
		// This is the part we abbreviated.
		if (i > mid || (j <= hi && array[j] < array[i]))
			aux[k++] = array[j++];
		else
			aux[k++] = array[i++];
	}
	
	// copy everything from the auxiliary array back into the original array
	for (i = lo; i <= hi; i++)
		array[i] = aux[i - lo];
	
	// clean up after yourself
	free(aux);
}

void printArray(int *array, int n)
{
	int i;

	for (i = 0; i < n; i++)
		printf("%d%c", array[i], (i == n - 1) ? '\n' : ' ');

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
	MergeSort(array, 0, LENGTH - 1);

	// Print the sorted array.
	printf("Sorted array:\n");
	printArray(array, LENGTH);

	return 0;
}
