// nSquaredSorts.c
// Jonathan Velez
// =================
// Implementations of Bubble, Selection, and Insertion sort.


#include <stdio.h>

// a function to swap elements at indices i and j within an array
void swap(int *array, int i, int j)
{
	int temp = array[i]; array[i] = array[j]; array[j] = temp;
}

// Bubble Sort. Terminates early if no swaps are made. 
void bubble_sort(int *array, int n)
{
	int i, j, swapped = 1;

	for (i = 0; i < n && swapped; i++)
	{
		swapped = 0;

		for (j = 0; j < n - i - 1; j++)
		{
			if (array[j] > array[j + 1])
			{
				swap(array, j, j + 1);
				swapped = 1;
			}
		}
	}
}

// Selection Sort. 'minIndex' is used to track the index of the minimum value.
void selection_sort(int *array, int n)
{
	int i, j, minIndex;

	for (i = 0; i < n - 1; i++)
	{
		minIndex = i;

		for (j = i + 1; j < n; j++)
			if (array[j] < array[minIndex])
				minIndex = j;

		swap(array, minIndex, i);
	}
}

// Insertion Sort. 'gap' is the index of the gap left as elements shift over,
// and 'val' is the element being inserted into the sorted portion of the array.
void insertion_sort(int *array, int n)
{
	int i, gap, val;

	for (i = 1; i < n; i++)
	{
		gap = i;
		val = array[i];

		while (gap > 0 && array[gap] < array[gap - 1])
		{
			swap(array, gap, gap - 1);
			gap--;
		}
		array[gap] = val;
	}
}

// Simple function to print an array.
void print_array(int *array, int n)
{
	int i;

	for (i = 0; i < n; i++)
		printf("%d%s", array[i], (i == n - 1) ? "\n" : ", ");
}

// Returns 1 if the array is unsorted, 0 otherwise.
int unsorted(int *array, int n)
{
	int i;

	for (i = 0; i < n - 1; i++)
		if (array[i] > array[i + 1])
			return 1;

	return 0;
}

int main(void)
{
	int array[10], n = 10, i, j;

	srand(time(NULL));

	// Populate an array with n random integers.
	for (i = 0; i < n; i++)
		array[i] = rand() % 100 + 1;

	// Print the unsorted array.
	print_array(array, n);

	// Call a sorting function.
	insertion_sort(array, n);

	// Print the sorted array.
	print_array(array, n);

	// Print a notification if the array isn't actually sorted at this point.
	if (unsorted(array, n))
		printf("ERROR: Array is unsorted!\n");

	return 0;
}
