/*
 * Copyright (c) 2016 Miles Sabin
 *
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

package cats
package derived

import alleycats.Empty
import cats.instances.all._
import shapeless.test.illTyped

class EmptySuite extends KittensSuite {
  import EmptySuite._
  import TestDefns._

  // `Monoid[Option[A]]` gives us `Empty[Option[A]]` but it requires a `Semigroup[A]`.
  implicit def emptyOption[A]: Empty[Option[A]] = Empty(None)

  def testEmpty(context: String)(
    implicit foo: Empty[Foo],
    outer: Empty[Outer],
    interleaved: Empty[Interleaved[String]],
    recursive: Empty[Recursive],
    iList: Empty[IList[Dummy]],
    snoc: Empty[Snoc[Dummy]],
    box: Empty[Box[Mask]],
    chain: Empty[Chain]
  ): Unit = {
    test(s"$context.Empty[Foo]")(assert(foo.empty == Foo(0, None)))
    test(s"$context.Empty[Outer]")(assert(outer.empty == Outer(Inner(0))))
    test(s"$context.Empty[Interleaved[String]]")(assert(interleaved.empty == Interleaved(0, "", 0, Nil, "")))
    test(s"$context.Empty[Recursive]")(assert(recursive.empty == Recursive(0, None)))
    test(s"$context.Empty[IList[Dummy]]")(assert(iList.empty == INil()))
    test(s"$context.Empty[Snoc[Dummy]]")(assert(snoc.empty == SNil()))
    test(s"$context.Empty respects existing instances")(assert(box.empty == Box(Mask(0xffffffff))))
    // Known limitation of recursive typeclass derivation.
    test(s"$context.Empty[Chain] throws a StackOverflowError")(assertThrows[StackOverflowError](chain.empty))
  }

  {
    import auto.empty._
    testEmpty("auto")
    illTyped("Empty[IList[Int]]")
    illTyped("Empty[Snoc[Int]]")
    illTyped("Empty[Rgb]")
  }

  {
    import cached.empty._
    testEmpty("cached")
    illTyped("Empty[IList[Int]]")
    illTyped("Empty[Snoc[Int]]")
    illTyped("Empty[Rgb]")
  }

  {
    implicit val foo: Empty[Foo] = semi.empty
    implicit val outer: Empty[Outer] = semi.empty
    implicit val interleaved: Empty[Interleaved[String]] = semi.empty
    implicit val recursive: Empty[Recursive] = semi.empty
    implicit lazy val iList: Empty[IList[Dummy]] = semi.empty
    implicit lazy val snoc: Empty[Snoc[Dummy]] = semi.empty
    implicit val box: Empty[Box[Mask]] = semi.empty
    implicit lazy val chain: Empty[Chain] = semi.empty
    testEmpty("semi")
    illTyped("semi.empty[IList[Int]]")
    illTyped("semi.empty[Snoc[Int]]")
    illTyped("semi.empty[Rgb]")
  }
}

object EmptySuite {

  trait Dummy
  final case class Chain(head: Int, tail: Chain)
  final case class Mask(bits: Int)
  object Mask {
    implicit val empty: Empty[Mask] = Empty(Mask(0xffffffff))
  }
}
