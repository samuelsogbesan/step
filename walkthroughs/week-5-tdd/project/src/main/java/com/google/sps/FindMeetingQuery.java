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
import java.util.Collection;

public final class FindMeetingQuery {
  /**
    * Determines the spaces within a 24 hour period occupied by attended events.
    * An event is considered attended if isAttended(event,request) returns true.
    *
    * // Attended Event:     |--B--|
    * // Day     : |---------------------|
    * // Output  : |----------1111111----|

    @param Event[] events the (potentially unattended) events during the day
    @param MeetingRequest request the meeting request used to determine event attendence.
    @return boolean[48] representing the day in 30 minute intervals, with occupied slots filled with true.
  */
  private static boolean[] getOccupiedTimes(Event[] events, MeetingRequest request) {
    final static int slots = 24 * 2; // 24 * 2 is the number of half hour slots in a day
    final static int THIRTY_MINUTES = 30; // The divisor used to split up the day
    
    boolean[] occupiedTimes = new boolean[slots]; // Output array of occupied times in the day.

    // Loop through events and mark slots as occupied if the event is attended.
    for(int i = 0; i < events.length; i ++) {
      // If an event is not attended, it does not need to be considered.
      if(!isAttended(events[i], request)) continue;

      TimeRange eventTime = events[i].getWhen();
      int eventStart = eventTime.start() / THIRTY_MINUTES;
      int eventEnd = (eventTime.start() + eventTime.duration()) / THIRTY_MINUTES - 1;
      int eventDuration = eventTime.duration() / THIRTY_MINUTES;

      // Indicate all the slots between eventStart and EventEnd as occupied.
      for(int j = eventStart  j <= eventEnd && j < slots; j++) occupiedTimes[j] = true;
    }
 
    return occupiedTimes;
  }

  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    throw new UnsupportedOperationException("TODO: Implement this method.");
  }
}
