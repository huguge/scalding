/*
 Copyright 2014 Twitter, Inc.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
package com.twitter.scalding.macros.impl

import scala.language.experimental.macros
import scala.reflect.macros.Context
import scala.util.Random

import com.twitter.scalding.typed.OrderedBufferable
import com.twitter.scalding.macros.impl.ordbufs._

object OrderedBufferableProviderImpl {
  def apply[T](c: Context)(implicit T: c.WeakTypeTag[T]): c.Expr[OrderedBufferable[T]] = {
    import c.universe._
    val primitiveDispatcher = PrimitiveOrderedBuf.dispatch(c)

    val dispatcher: PartialFunction[Type, TreeOrderedBuf[c.type]] = primitiveDispatcher.orElse {
      case t: Type => c.abort(c.enclosingPosition, s"""Unable to find OrderedBufferable for type ${T.tpe}""")
    }

    val b: TreeOrderedBuf[c.type] = dispatcher(T.tpe)
    TreeOrderedBuf.toOrderedBufferable[T](c)(b)
  }
}