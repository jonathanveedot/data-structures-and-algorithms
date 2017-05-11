#include <stdlib.h>
#include "queue.h"
#include "node.h"

Queue *initQueue(void)
{
	Queue *p = malloc(sizeof(Queue));

	p->head = NULL;
	p->tail = NULL;

	return p;
}

// Insert a new node at the back of the queue (i.e., the tail of the list).
// Return -1 if insertion fails, or 'data' if insertion succeeds. This makes
// a call to the createNode() function defined in "node.c".
int enqueue(Queue *p, int data)
{
	if (p == NULL)
	{
		return -1;
	}

	if (isEmpty(p))
	{
		p->head = p->tail = createNode(data);
		if (p->tail == NULL)
		{
			return -1;
		}
	}
	else
	{
		p->tail->next = createNode(data);
		if (p->tail->next == NULL)
		{
			return -1;
		}
		p->tail = p->tail->next;
	}

	return data;
}

// Remove the element at the front of the list (i.e., the head of the list).
// Return the 'data' stored in the dequeued node, or -1 if the queue is empty.
// Keep in mind that you need to free() the node being removed to avoid memory
// leaks.
int dequeue(Queue *p)
{
	int retval;
	node *temp;

	if (p == NULL || isEmpty(p))
		return -1;

	retval = p->head->data;

	temp = p->head;
	p->head = p->head->next;
	free(temp);

	return retval;
}

int isEmpty(Queue *p)
{
	return (p->head == NULL);
}
