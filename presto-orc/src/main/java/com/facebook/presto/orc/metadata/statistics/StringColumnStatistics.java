/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.facebook.presto.orc.metadata.statistics;

import com.google.common.base.MoreObjects.ToStringHelper;
import org.openjdk.jol.info.ClassLayout;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

public class StringColumnStatistics
        extends ColumnStatistics
{
    private static final int INSTANCE_SIZE = ClassLayout.parseClass(StringColumnStatistics.class).instanceSize();

    private final StringStatistics stringStatistics;

    public StringColumnStatistics(
            Long numberOfValues,
            long minAverageValueSizeInBytes,
            HiveBloomFilter bloomFilter,
            StringStatistics stringStatistics)
    {
        super(numberOfValues, minAverageValueSizeInBytes, bloomFilter);
        requireNonNull(stringStatistics, "stringStatistics is null");
        this.stringStatistics = stringStatistics;
    }

    @Override
    public StringStatistics getStringStatistics()
    {
        return stringStatistics;
    }

    @Override
    public ColumnStatistics withBloomFilter(HiveBloomFilter bloomFilter)
    {
        return new StringColumnStatistics(
                getNumberOfValues(),
                getMinAverageValueSizeInBytes(),
                bloomFilter,
                stringStatistics);
    }

    @Override
    public long getRetainedSizeInBytes()
    {
        long sizeInBytes = INSTANCE_SIZE + getMembersSizeInBytes();
        return sizeInBytes + stringStatistics.getRetainedSizeInBytes();
    }

    @Override
    public void addHash(StatisticsHasher hasher)
    {
        super.addHash(hasher);
        hasher.putOptionalHashable(stringStatistics);
    }

    @Override
    protected ToStringHelper getToStringHelper()
    {
        return super.getToStringHelper()
                .add("stringStatistics", stringStatistics);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        StringColumnStatistics that = (StringColumnStatistics) o;
        return equalsInternal(that) && Objects.equals(stringStatistics, that.stringStatistics);
    }

    public int hashCode()
    {
        return Objects.hash(super.hashCode(), stringStatistics);
    }
}
