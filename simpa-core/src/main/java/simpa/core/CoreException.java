/*
 * @(#)CoreException.java   2009.08.06 at 09:03:35 PDT
 *
 * Copyright 2009 MBARI
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package simpa.core;

/**
 * For some usages it's nice to wrap Exception with RuntimeExceptions. This
 * is the base excepton for the Simpa Exception hierarchy
 *
 * @author brian
 */
public class CoreException extends RuntimeException {

    /**
     * Constructs ...
     */
    public CoreException() {}

    /**
     * Constructs ...
     *
     * @param message
     */
    public CoreException(String message) {
        super(message);
    }

    /**
     * Constructs ...
     *
     * @param cause
     */
    public CoreException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs ...
     *
     * @param message
     * @param cause
     */
    public CoreException(String message, Throwable cause) {
        super(message, cause);
    }
}
