// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import com.google.sps.TimeRange;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class FindMeetingQuery {
  /**
    * Determines the spaces within a 24 hour period occupied by attended events.
    * An event is considered attended if isAttended(event,request) returns true.
    *
    * // Attended Event:     |--B--|
    * // Day     : |---------------------|
    * // Output  : |---------1111111----|

    @param Event[] events the (potentially unattended) events during the day
    @param MeetingRequest request the meeting request used to determine event attendence.
    @return boolean[48] representing the day in 30 minute interval
    * a value at a specific index indicates whether the corresponding interval is occupied.
  */
  protected static boolean[] getOccupiedSlotsInDay(Event[] events, MeetingRequest request) {
    final int SLOTS = 24 * 2; // 24 * 2 is the number of half hour slots in a day.
    final int THIRTY_MINUTES = 30; // The divisor used to split up the day.
    
    boolean[] occupiedTimes = new boolean[SLOTS]; // Output array of occupied times in the day.

    // Mark slots in occupiedTimes as true if an attended event occurs there.
    for (int i = 0; i < events.length; i++) {
      // If an event is not attended, it does not need to be considered.
      if (!isAttended(events[i], request)) continue;

      TimeRange eventTime = events[i].getWhen();
      int eventStart = eventTime.start() / THIRTY_MINUTES;
      int eventEnd = (eventTime.end()) / THIRTY_MINUTES - 1;

      // Indicate all the slots between eventStart and EventEnd as occupied.
      for (int j = eventStart; j <= eventEnd && j < SLOTS; j++) occupiedTimes[j] = true;
    }
 
    return occupiedTimes;
  }

  /**
    * Returns whether an event is attended by the required attendees from the Meeting Request.
    *
    @param Event event The input event to compare against
    @param MeetingRequest request The subject meeting request
    @return a boolean value that is true if the event and request have overlapping attendees.
  */
  private static boolean isAttended(Event event, MeetingRequest request) {
    Collection<String> eventAttendees = event.getAttendees();
    Collection<String> requestAttendees = request.getAttendees();
    for (String attendee : eventAttendees) if (requestAttendees.contains(attendee)) return true;

    // Fallback if no attendees are found that belong to both the event and the meeting request.
    return false;
  }

  /**
    * Returns the minutes in a day from a half hour slot.
    *
    * @param int halfHour The halfhour slot to be converted.
    * @exception IllegalArgumentException thrown if slot < 0 or slot > 47.
    * @return int timestamp in minutes.
  */
  protected static int convertToMinuteTimestamp(int halfHour) {
    if(halfHour < 0 || halfHour > 48) throw new IllegalArgumentException("Time out of bounds.");
    
    return halfHour * 30;
  }

  /**
   * Calculates the amount of free space in a day for a meeting to take place.
   * @param Collection<Event> input 
   * @param MeetingRequest request the input meeting to be
   * @return a collection of TimeRanges indicates free time.
   */
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    List<TimeRange> freeTimes = new ArrayList<TimeRange>(); // List for possible meeting times.
    Event[] eventsArray = events.toArray(new Event[events.size()]);
    long duration = request.getDuration() / 30; // Meeting duration in number of half hour slots.
    
    // If the duration is out of bounds, we don't need to add any times.
    if (duration <= 0 ) {
      freeTimes.add(TimeRange.WHOLE_DAY);
      return freeTimes;
    }
    if (duration > 47) return freeTimes;

    boolean[] occupiedTimes = getOccupiedSlotsInDay(eventsArray, request);
 
    int lowerBound = 0; // Start of Time Gap (first unoccupied slot past endPointer).
    int upperBound = 0; // End of Time Gap (last unoccupied slot after lowerBound).
 
    // Loop through array and find gaps between occupied slots.
    // endPointer points at the first most scanned element in the array.
    // If gaps are sufficiently large, add them to list.
    for (int endPointer = 0; endPointer < occupiedTimes.length; endPointer++) {
      boolean slot = occupiedTimes[endPointer];

      // If we find an occupied slot, set our gap upper bound to that slot.
      if (slot == true) upperBound = endPointer;
      // Increase search bound by one to include the last element.
      else if(endPointer == occupiedTimes.length-1) upperBound = endPointer+1; 
 
      if (slot == true || endPointer == occupiedTimes.length-1) {
        // The range between these two values is the duration of the gap.
        int gapSize = upperBound - lowerBound;
        
        // If the gap is sufficiently large, add a TimeRange from those bounds to our output list.
        if (gapSize >= duration) {
          freeTimes.add(TimeRange.fromStartEnd(convertToMinuteTimestamp(lowerBound),
                convertToMinuteTimestamp(upperBound),false));
        }
 
        // Fast forward lowerBound to next free space and update endPointer to continue from there.
        for (lowerBound = endPointer; 
            lowerBound < occupiedTimes.length && occupiedTimes[lowerBound] == true; 
            lowerBound++);
        endPointer = lowerBound;
      }
    }
 
    return freeTimes;
  }

}
