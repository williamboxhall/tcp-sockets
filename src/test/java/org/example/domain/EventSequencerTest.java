package org.example.domain;

import static java.lang.String.format;
import static org.mockito.Matchers.refEq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;

import org.example.infrastructure.Consumer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class EventSequencerTest {
	@Mock
	private Consumer<Event> consumer;

	@Test
	public void sendsEventsInOrderToConsumer() {
		EventSequencer sequencer = EventSequencer.sendTo(consumer);
		sequencer.accept("2|B");
		verifyZeroInteractions(consumer);
		sequencer.accept("1|B");
		InOrder inOrder = inOrder(consumer);
		inOrder.verify(consumer).accept(refEq(new Event("1|B")));
		inOrder.verify(consumer).accept(refEq(new Event("2|B")));
	}

	@Test
	public void dropsMalfomedEvents() {
		EventSequencer sequencer = EventSequencer.sendTo(consumer);
		sequencer.accept("1|B");
		sequencer.accept("quijibo");
		verify(consumer).accept(refEq(new Event("1|B")));
		verifyNoMoreInteractions(consumer);
	}

	@Test
	public void sequenceNumbersCanBeLargerThan32Bits() {
		long largeSequenceNumber = Integer.MAX_VALUE + 10L;
		EventSequencer sequencer = new EventSequencer(consumer, largeSequenceNumber - 1);
		sequencer.accept(format("%d|B", largeSequenceNumber));
		verify(consumer).accept(refEq(new Event(format("%d|B", largeSequenceNumber))));
	}
}